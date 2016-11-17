package com.smile.makeyourteam.Fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.smile.makeyourteam.Models.Group;
import com.smile.makeyourteam.R;
import com.smile.makeyourteam.server.Firebase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateGroupDialogFragment extends DialogFragment implements View.OnClickListener {

    Button btnOk;
    Button btnCancel;
    EditText editGroupName;

    public CreateGroupDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_group_dialog, container, false);
        getDialog().setTitle("New group");
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
            editGroupName.setError("Please input group name");
            return;
        }

        DatabaseReference mDatabase = Firebase.database.getReference();
        FirebaseUser mUser = Firebase.firebaseAuth.getCurrentUser();
        if (mDatabase == null || mUser == null)
            return;

        String key = mDatabase.child("message").push().getKey();
        Group group = new Group(key, groupName, new Date().getTime(), "");
        Map<String, Object> groupValue = group.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + mUser.getUid() + "/groups/" + key, groupValue);
        mDatabase.updateChildren(childUpdates);
    }
}
