package com.smile.makeyourteam.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.smile.makeyourteam.R;

public class CreateTeamActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnCreateNewTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        btnCreateNewTeam = (Button) findViewById(R.id.btnCreateNewTeam);

        btnCreateNewTeam.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btnCreateNewTeam) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
        }
    }
}
