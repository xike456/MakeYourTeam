package com.smile.makeyourteam.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.smile.makeyourteam.R;

public class JoinTeamActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnCreateTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_team);

        btnCreateTeam = (Button) findViewById(R.id.btnCreateTeam);

        btnCreateTeam.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btnCreateTeam) {
            Intent intent = new Intent(this, CreateTeamActivity.class);
            startActivity(intent);
        }
    }
}
