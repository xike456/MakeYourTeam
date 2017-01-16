package com.smile.makeyourteam.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smile.makeyourteam.Fragments.TaskManagerFragment;
import com.smile.makeyourteam.Models.User;
import com.smile.makeyourteam.R;
import com.smile.makeyourteam.server.Firebase;
import com.smile.makeyourteam.services.Notifications;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        FirebaseUser user = Firebase.firebaseAuth.getCurrentUser();
        if (user != null) {
            getCurrentUser();
            Log.d("Authentication",  "onAuthStateChanged:signed_in:" + user.getUid());
        } else {
            // User is signed out
            StartLogin();
            Log.d("Authentication", "onAuthStateChanged:signed_out");
        }
    }

    private void StartLogin() {
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
        finish();
    }

    private void getCurrentUser() {
        FirebaseUser mUser = Firebase.firebaseAuth.getCurrentUser();
        final DatabaseReference database = Firebase.database.getReference("users").child(mUser.getUid());
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MainActivity.currentUser = dataSnapshot.getValue(User.class);
                if (MainActivity.currentUser == null) return;
                if (MainActivity.currentUser.teamId == null || MainActivity.currentUser.teamId.isEmpty()) {
                    database.removeEventListener(this);
                    startJoinTeam();
                } else {
                    database.removeEventListener(this);
                    startMainActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void startMainActivity() {
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
        this.finish();
    }

    private void startJoinTeam() {
        Intent startJoinTeamActivity = new Intent(this, JoinTeamActivity.class);
        startActivity(startJoinTeamActivity);
        this.finish();
    }
}
