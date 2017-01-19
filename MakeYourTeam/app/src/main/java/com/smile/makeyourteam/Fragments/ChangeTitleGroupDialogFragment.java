package com.smile.makeyourteam.Fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smile.makeyourteam.Activities.MainActivity;
import com.smile.makeyourteam.Models.Group;
import com.smile.makeyourteam.R;
import com.smile.makeyourteam.server.Firebase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by NgoChiHai on 11/24/16.
 */

public class ChangeTitleGroupDialogFragment extends DialogFragment implements View.OnClickListener{
    Button btnOk;
    Button btnCancel;
    EditText editGroupName;
    public static String GroupID;

    public ChangeTitleGroupDialogFragment() {
        // Required empty public constructor

    }

    public void setGroupID(String GroupID) {
        this.GroupID = GroupID;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_title_group, container, false);
        getDialog().setTitle("Change title group");
        btnOk = (Button) view.findViewById(R.id.btnOkNewGroup);
        btnCancel = (Button) view.findViewById(R.id.btnCancelNewGroup);
        editGroupName = (EditText) view.findViewById(R.id.editGroupName);

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        if(view == btnCancel) {
            dismiss();
        } else if (view == btnOk) {
            createNewGroup();
            dismiss();
        }
    }

    private void createNewGroup() {
        String groupName = String.valueOf(editGroupName.getText());
        if (groupName == "") {
            editGroupName.setError("Please input title group");
            return;
        }

        DatabaseReference mDatabase = Firebase.database.getReference();
        FirebaseUser mUser = Firebase.firebaseAuth.getCurrentUser();
        if (mDatabase == null || mUser == null)
            return;

        DatabaseReference database = Firebase.database.getReference("users");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if(ds.child("groups").child(GroupID).exists()){
                        DatabaseReference databaseRef = Firebase.database.getReference("users").child(ds.getKey()).child("groups").child(GroupID);
                        databaseRef.child("title").setValue(editGroupName.getText().toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
