package com.smile.makeyourteam.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smile.makeyourteam.Activities.ChatActivity;
import com.smile.makeyourteam.Activities.MainActivity;
import com.smile.makeyourteam.Config;
import com.smile.makeyourteam.Models.Message;
import com.smile.makeyourteam.Models.User;
import com.smile.makeyourteam.Adapters.UserAdapter;
import com.smile.makeyourteam.R;
import com.smile.makeyourteam.server.Firebase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatUserFragment extends Fragment {

    private ListView lvUser;
    private UserAdapter userAdapter;

    private List<User> uList;

    public ChatUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View view = inflater.inflate(R.layout.fragment_chat_user, container, false);
        lvUser = (ListView) view.findViewById(R.id.listUsers);
       // lvUser.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        uList = new ArrayList<>();

        LoadUser();
        return view;
    }

    void LoadUser(){
        final DatabaseReference database = Firebase.database.getReference("users");
        Query myTeamQuery = database.orderByChild("teamId").startAt(MainActivity.currentUser.teamId).endAt(MainActivity.currentUser.teamId);
        myTeamQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User currentUser = new User();
                uList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if(!user.id.contentEquals(Firebase.firebaseAuth.getCurrentUser().getUid())) {
                        uList.add(user);
                    }else{
                        currentUser = user;
                    }
                }

                DatabaseReference lastMessageDB = database.child(currentUser.id).child("lastMessages");
                final User finalCurrentUser = currentUser;
                lastMessageDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()) {
                            Message message = ds.getValue(Message.class);
                            if (message.isNotify) {
                                for (int j = 0; j < uList.size(); j++) {
                                    if (uList.get(j).id.equals(message.senderId)) {
                                        uList.get(j).isNotify = message.isNotify;
                                    }
                                }
                            }
                        }


                        uList = sortByTimeStamp(uList);

                        userAdapter = new UserAdapter(getActivity(), R.layout.user_item, uList);

                        lvUser.setAdapter(userAdapter);
                        lvUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                database.child(finalCurrentUser.id).child("lastMessages")
                                        .child(((User) parent.getAdapter().getItem(position)).id)
                                        .child("isNotify").setValue(false);
                                Intent i = new Intent(getContext(),ChatActivity.class);
                                i.putExtra(Config.ID_USER_REVEIVE, uList.get(position).id);
                                i.putExtra(Config.NAME_USER_RECEIVE,uList.get(position).displayName);
                                i.putExtra(Config.USER_NAME,getUserName(finalCurrentUser));
                                i.putExtra(Config.PHOTO_URL, finalCurrentUser.thumbnail);
                                startActivity(i);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private List<User> sortByTimeStamp(List<User> uList) {
        Collections.sort(uList, new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                return (new Date(user2.lastMessageTimeStamp)).compareTo((new Date(user1.lastMessageTimeStamp)));
            }
        });
        return uList;
    }

    private String getUserName(User user){
        if(user.displayName!=null)
            return user.displayName;
        if(user.nickName!=null)
            return user.nickName;

        return user.email;
    }
}
