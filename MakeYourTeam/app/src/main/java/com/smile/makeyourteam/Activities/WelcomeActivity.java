package com.smile.makeyourteam.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.smile.makeyourteam.R;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnNextToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        btnNextToHome = (Button) findViewById(R.id.btnNextToHome);

        btnNextToHome.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btnNextToHome) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}
