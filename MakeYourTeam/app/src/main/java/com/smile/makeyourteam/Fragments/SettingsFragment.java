package com.smile.makeyourteam.Fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smile.makeyourteam.Activities.MainActivity;
import com.smile.makeyourteam.Models.Team;
import com.smile.makeyourteam.Models.User;
import com.smile.makeyourteam.R;
import com.smile.makeyourteam.server.Firebase;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {


    private EditText editDisplayName;
    private EditText editNickname;
    private EditText editEmail;
    private EditText editPIN;
    private EditText editTeamName;
    private FirebaseUser mUser;
    private User dbUser;
    private Team dbTeam;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        editDisplayName = (EditText) view.findViewById(R.id.editDisplayNameSettings);
        editNickname = (EditText) view.findViewById(R.id.editNicknameSettings);
        editEmail = (EditText) view.findViewById(R.id.editEmailSettings);

        editTeamName = (EditText) view.findViewById(R.id.editTeamNameSettings);
        editPIN = (EditText) view.findViewById(R.id.editPinTeamSettings);


        loadUserInfo();

        return view;
    }

    private void loadUserInfo() {
        mUser = Firebase.firebaseAuth.getCurrentUser();
        if (mUser != null) {
            DatabaseReference databaseUser = Firebase.database.getReference().child("users").child(mUser.getUid());
            databaseUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dbUser = dataSnapshot.getValue(User.class);
                    loadTeam();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void loadTeam() {
        if (dbUser == null) return;
        DatabaseReference databaseUser = Firebase.database.getReference().child("teams").child(dbUser.teamId);
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dbTeam = dataSnapshot.getValue(Team.class);
                setToView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setToView() {
        editDisplayName.setText(dbUser.displayName);
        editNickname.setText(dbUser.nickName);
        editEmail.setText(dbUser.email);
        editTeamName.setText(dbTeam.teamName);
        editPIN.setText(dbTeam.id);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_settings, menu);
    }
}
