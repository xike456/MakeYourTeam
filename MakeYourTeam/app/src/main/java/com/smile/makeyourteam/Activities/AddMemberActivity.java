package com.smile.makeyourteam.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smile.makeyourteam.Adapters.UserAdapter;
import com.smile.makeyourteam.Config;
import com.smile.makeyourteam.Models.Group;
import com.smile.makeyourteam.Models.User;
import com.smile.makeyourteam.R;
import com.smile.makeyourteam.server.Firebase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddMemberActivity extends AppCompatActivity {

    private ListView lvMember;
    private UserAdapter userAdapter;
    private Button btnAdd;
    private List<User> uList;
    private String groupName;
    private String groupID;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        intent = getIntent();
        groupID = intent.getStringExtra(Config.ID_GROUP);
        groupName = intent.getStringExtra(Config.NAME_GROUP);

        lvMember = (ListView) findViewById(R.id.listMember);
        lvMember.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        uList = new ArrayList<>();
        LoadUser();

        btnAdd = (Button) findViewById(R.id.btn_add_member);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Boolean> checked = userAdapter.positionChecked;
                for (int i = 0; i < checked.size(); i++) {
                    if (checked.get(i)){
                        addMemberToGroup(groupName, groupID, uList.get(i).id);
                        Toast.makeText(AddMemberActivity.this,"Add member success",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });
    }

    void LoadUser(){
        DatabaseReference database = Firebase.database.getReference("users");
        Query myTopPostsQuery = database.orderByChild("teamId").startAt(MainActivity.currentUser.teamId).endAt(MainActivity.currentUser.teamId);
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Toast.makeText(AddMemberActivity.this,"myTopPostsQuery chay ",Toast.LENGTH_SHORT).show();
                uList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    final User user = ds.getValue(User.class);
                    DatabaseReference database = Firebase.database.getReference("users").child(user.id).child("groups").child(groupID);
                    database.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                          // String flag = "false";
                            if(!dataSnapshot.exists()){
                              //  flag = "true";
                                if(!user.id.contentEquals(Firebase.firebaseAuth.getCurrentUser().getUid())) {
                                    uList.add(user);
                                }
                              //  Toast.makeText(AddMemberActivity.this,flag,Toast.LENGTH_SHORT).show();

                                userAdapter = new UserAdapter(AddMemberActivity.this, R.layout.member_item, uList);
                                lvMember.setAdapter(userAdapter);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addMemberToGroup(String groupName, String key, String mUserId) {
        DatabaseReference mDatabase = Firebase.database.getReference();
        if (mDatabase == null || mUserId == null)
            return;

        Group group = new Group(key, groupName, new Date().getTime(), "");

        Map<String, Object> groupValue = group.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + mUserId + "/groups/" + key, groupValue);
        mDatabase.updateChildren(childUpdates);
    }
}
