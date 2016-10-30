package com.smile.makeyourteam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.smile.makeyourteam.models.Group;
import com.smile.makeyourteam.server.Firebase;

import java.util.HashMap;
import java.util.Map;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnCreate;
    Button btnCancel;
    EditText txtGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        btnCreate = (Button) findViewById(R.id.btnCreate);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        txtGroupName = (EditText) findViewById(R.id.txtGroupName);
        btnCreate.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnCreate) {
            String groupName = String.valueOf(txtGroupName.getText());
            if (groupName == "") {
                Toast.makeText(this, "Please input group name", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference mDatabase = Firebase.database.getReference();
            FirebaseUser mUser = Firebase.firebaseAuth.getCurrentUser();
            if (mDatabase == null || mUser == null)
                return;

            String key = mDatabase.child("message").push().getKey();
            Group group = new Group(key, groupName);
            Map<String, Object> groupValue = group.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/users/" + mUser.getUid() + "/groups/" + key, groupValue);
            mDatabase.updateChildren(childUpdates);
            this.finish();
        } else if (v == btnCancel) {
            this.finish();
        }
    }
}
