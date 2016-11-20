package com.smile.makeyourteam.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smile.makeyourteam.Config;
import com.smile.makeyourteam.R;
import com.smile.makeyourteam.server.Firebase;

public class JoinTeamActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnCreateTeam;
    Button btnJoin;
    EditText editPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_team);

        editPin = (EditText) findViewById(R.id.editPin);
        btnCreateTeam = (Button) findViewById(R.id.btnCreateTeam);
        btnJoin = (Button) findViewById(R.id.btnJoin);

        btnJoin.setOnClickListener(this);
        btnCreateTeam.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btnCreateTeam) {
            Intent intent = new Intent(this, CreateTeamActivity.class);
            startActivity(intent);
        } else if (view == btnJoin) {
            joinNewTeam();
        }
    }

    private void joinNewTeam() {
        String pin = String.valueOf(editPin.getText());
        if (pin.equals("")) {
            editPin.setError("Please input PIN code!");
            Toast.makeText(this, "Please input PIN code!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference mDatabase = Firebase.database.getReference();
        FirebaseUser mUser = Firebase.firebaseAuth.getCurrentUser();
        if (mDatabase == null || mUser == null)
            return;

        DatabaseReference teamObj = mDatabase.child("teams").child(pin);
        teamObj.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object team = dataSnapshot.getValue();
                if (team == null) {
                    editPin.requestFocus();
                    editPin.setError("PIN is not correct!");
                    Toast.makeText(getApplicationContext(), "PIN is not correct!", Toast.LENGTH_SHORT).show();
                } else {
                    joinTeamSuccess();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void joinTeamSuccess() {
        String pin = String.valueOf(editPin.getText());
        if (pin == "") {
            Toast.makeText(this, "Please input PIN code", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference mDatabase = Firebase.database.getReference();
        FirebaseUser mUser = Firebase.firebaseAuth.getCurrentUser();
        if (mDatabase == null || mUser == null)
            return;

        mDatabase.child("users").child(mUser.getUid()).child("teamId").setValue(pin);
        startWelcome(pin);
    }

    private void startWelcome(String pin) {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.putExtra(Config.PIN_TEAM, pin);
        startActivity(intent);
        this.finish();
    }
}
