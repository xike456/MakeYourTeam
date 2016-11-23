package com.smile.makeyourteam.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smile.makeyourteam.Activities.ChatActivity;
import com.smile.makeyourteam.Activities.MainActivity;
import com.smile.makeyourteam.Config;
import com.smile.makeyourteam.Models.User;
import com.smile.makeyourteam.Adapters.UserAdapter;
import com.smile.makeyourteam.R;
import com.smile.makeyourteam.server.Firebase;

import java.util.ArrayList;
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
        DatabaseReference database = Firebase.database.getReference("users");
        Query myTopPostsQuery = database.orderByChild("teamId").startAt(MainActivity.teamId).endAt(MainActivity.teamId);
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
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

                userAdapter = new UserAdapter(getActivity(), R.layout.user_item, uList);

                lvUser.setAdapter(userAdapter);
                final User finalCurrentUser = currentUser;
                lvUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

    private String getUserName(User user){
        if(user.displayName!=null)
            return user.displayName;
        if(user.nickName!=null)
            return user.nickName;

        return user.email;
    }
}
