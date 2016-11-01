package com.smile.makeyourteam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smile.makeyourteam.models.Group;
import com.smile.makeyourteam.models.User;
import com.smile.makeyourteam.server.Firebase;

import java.util.ArrayList;
import java.util.List;

public class AddUsersToGroupActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnAdd;
    Button btnRemove;
    Button btnCancel;
    ListView mListUser;
    private List<User> uUsers = new ArrayList<User>();
    private ArrayAdapter<String> adapterUser;
    private FirebaseUser user;
    private Group groupInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_users_to_group);

        btnAdd = (Button) findViewById(R.id.btnAddToGroup);
        btnRemove = (Button) findViewById(R.id.btnRemoveFromGroup);
        btnCancel = (Button) findViewById(R.id.btnCancelAddUser);
        mListUser = (ListView) findViewById(R.id.listUserAdd);

        btnAdd.setOnClickListener(this);
        btnRemove.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        user = Firebase.firebaseAuth.getCurrentUser();

        if (user != null) {
            LoadUser();
        } else {
            StartLogin();
        };

        Intent i = getIntent();
        groupInfo = (Group) i.getSerializableExtra(Config.GROUP_INFO);
    }

    private void LoadUser() {
        DatabaseReference database = Firebase.database.getReference("users");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uUsers.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if(!user.id.contentEquals(Firebase.firebaseAuth.getCurrentUser().getUid())) {
                        uUsers.add(user);
                    }
                }

                final ArrayList<String> emails = new ArrayList<String>();
                for (int i = 0; i< uUsers.size(); i++) {
                    emails.add(uUsers.get(i).email);
                }

                adapterUser = new ArrayAdapter<String>(AddUsersToGroupActivity.this, android.R.layout.simple_list_item_multiple_choice, emails);

                mListUser.setAdapter(adapterUser);
                mListUser.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void StartLogin() {
        Intent i = new Intent(this, Login.class);
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        if (v == btnAdd) {
            SparseBooleanArray listUser = mListUser.getCheckedItemPositions();
            DatabaseReference dbUsers = Firebase.database.getReference("users");

            for(int i = 0;i < listUser.size(); i++) {
                String id = uUsers.get(listUser.keyAt(i)).id;
                dbUsers.child(id).child("groups").child(groupInfo.id).setValue(groupInfo);
            }
            this.finish();
        } else if (v == btnRemove) {
            SparseBooleanArray listUser = mListUser.getCheckedItemPositions();
            DatabaseReference dbUsers = Firebase.database.getReference("users");

            for(int i = 0;i < listUser.size(); i++) {
                String id = uUsers.get(listUser.keyAt(i)).id;
                dbUsers.child(id).child("groups").child(groupInfo.id).removeValue();
            }
            this.finish();
        } else if (v == btnCancel) {
            this.finish();
        }
    }
}
