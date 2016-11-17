package com.smile.makeyourteam.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.smile.makeyourteam.Config;
import com.smile.makeyourteam.Models.Team;
import com.smile.makeyourteam.R;
import com.smile.makeyourteam.server.Firebase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateTeamActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnCreateNewTeam;
    EditText editTeamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        FirebaseUser user = Firebase.firebaseAuth.getCurrentUser();
        if (user == null) {
            startLogin();
            Log.d("Authentication", "onAuthStateChanged:signed_out");
        }

        editTeamName = (EditText) findViewById(R.id.editTeamName);
        btnCreateNewTeam = (Button) findViewById(R.id.btnCreateNewTeam);

        btnCreateNewTeam.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btnCreateNewTeam) {
            createNewTeam();
        }
    }

    private void createNewTeam() {
        String teamName = String.valueOf(editTeamName.getText());
        if (teamName.isEmpty()) {
            editTeamName.setError("Please input team name");
            return;
        }

        DatabaseReference mDatabase = Firebase.database.getReference();
        FirebaseUser mUser = Firebase.firebaseAuth.getCurrentUser();
        if (mDatabase == null || mUser == null)
            return;

        String PIN = createNewPIN();
        Team group = new Team(PIN, teamName);
        mDatabase.child("teams/" + PIN).setValue(group);
        mDatabase.child("users/" + mUser.getUid() + "/teamId").setValue(group.id);
        startWelcome(PIN);
        this.finish();
    }

    private void startWelcome(String pin) {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.putExtra(Config.PIN_TEAM, pin);
        startActivity(intent);
        this.finish();
    }

    private String createNewPIN() {
        return UUID.randomUUID().toString().replace("-","").substring(0, 6).toUpperCase();
    }

    // function call activity login
    private void startLogin() {
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
        finish();
    }
}
