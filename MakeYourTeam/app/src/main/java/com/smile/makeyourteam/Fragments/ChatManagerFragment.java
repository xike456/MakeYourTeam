package com.smile.makeyourteam.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.smile.makeyourteam.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatManagerFragment extends Fragment {

    boolean isGroupView;


    public ChatManagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.viewChatManager, new ChatGroupFragment()).addToBackStack(null).commit();
        isGroupView = true;
        return inflater.inflate(R.layout.fragment_chat_manager, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_chat_mangager, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnSwitchChat:
                if (isGroupView) {
                    isGroupView = false;
                    item.setIcon(R.drawable.ic_person_white_24dp);
                    item.setTitle("Users");
                    LoadUserChatView();
                } else {
                    isGroupView = true;
                    item.setIcon(R.drawable.ic_people_white_24dp);
                    item.setTitle("Groups");
                    LoadGroupChatView();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void LoadUserChatView() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.viewChatManager, new ChatUserFragment()).addToBackStack(null).commit();
    }

    private void LoadGroupChatView() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.viewChatManager, new ChatGroupFragment()).addToBackStack(null).commit();
    }
}
