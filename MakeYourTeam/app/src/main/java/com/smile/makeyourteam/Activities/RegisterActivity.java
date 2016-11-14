package com.smile.makeyourteam.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.smile.makeyourteam.R;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = (Button) findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == btnRegister) {
            Intent intent = new Intent(this, JoinTeamActivity.class);
            startActivity(intent);
        }
    }
}
