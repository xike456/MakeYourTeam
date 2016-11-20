package com.smile.makeyourteam.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smile.makeyourteam.Config;
import com.smile.makeyourteam.Models.Team;
import com.smile.makeyourteam.R;
import com.smile.makeyourteam.server.Firebase;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnNextToHome;
    TextView txtTeamName;
    TextView txtPINCode;
    private ProgressDialog progressDialog;
    private String PIN = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        btnNextToHome = (Button) findViewById(R.id.btnNextToHome);
        txtPINCode = (TextView) findViewById(R.id.txtPINCode);
        txtTeamName = (TextView) findViewById(R.id.txtTeamNameWelcome);

        btnNextToHome.setOnClickListener(this);

        FirebaseUser currentUser = Firebase.firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            startLogin();
        }

        PIN = getIntent().getStringExtra(Config.PIN_TEAM);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading team...");
        progressDialog.show();
        loadTeamInfo();
    }

    private void loadTeamInfo() {
        DatabaseReference mDatabase = Firebase.database.getReference();
        FirebaseUser mUser = Firebase.firebaseAuth.getCurrentUser();
        if (mDatabase == null || mUser == null)
            return;

        DatabaseReference dataRef = mDatabase.child("teams").child(PIN);
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Team team = dataSnapshot.getValue(Team.class);
                txtPINCode.setText(team.id);
                txtTeamName.setText(team.teamName);
                progressDialog.hide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void startLogin() {
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == btnNextToHome) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
    }
}
