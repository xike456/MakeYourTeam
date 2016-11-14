package com.smile.makeyourteam.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.smile.makeyourteam.Activities.ChatActivity;
import com.smile.makeyourteam.Models.User;
import com.smile.makeyourteam.Adapters.UserAdapter;
import com.smile.makeyourteam.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatUserFragment extends Fragment {


    private List<User> usersList = new ArrayList<>();
    private ListView lvUser;
    private UserAdapter userAdapter;

    public ChatUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View view = inflater.inflate(R.layout.fragment_chat_user, container, false);
        usersList.add(new User("","Phu","", "@phu" ,R.drawable.drawer_top));
        usersList.add(new User("","Quang", "" ,"@quang" ,R.drawable.ic_people_black_48dp));
        usersList.add(new User("","Vu","", "@vu",R.drawable.drawer_top));
        usersList.add(new User("","Hai","", "@hai",R.drawable.ic_people_black_48dp));
        usersList.add(new User("","Linh","", "@linh",R.drawable.ic_people_black_48dp));


        lvUser = (ListView) view.findViewById(R.id.listUsers);

        userAdapter = new UserAdapter(getActivity(), R.layout.user_item, usersList);
        lvUser.setAdapter(userAdapter);
        lvUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
