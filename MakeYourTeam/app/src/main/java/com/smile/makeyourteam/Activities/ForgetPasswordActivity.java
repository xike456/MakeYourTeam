package com.smile.makeyourteam.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.smile.makeyourteam.R;
import com.smile.makeyourteam.server.Firebase;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtEmail;
    private Button btnSendEmail;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        txtEmail = (EditText) findViewById(R.id.editEmailForget);
        btnSendEmail = (Button) findViewById(R.id.btnSendEmail);

        progressDialog = new ProgressDialog(this);

        btnSendEmail.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v==btnSendEmail){
            SendResetEmail();
        }
    }

    private void SendResetEmail() {
        String email = txtEmail.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            txtEmail.setError("Please enter email");
            return;
        }

        progressDialog.setMessage("Sending email...");
        progressDialog.show();

        Firebase.firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ForgetPasswordActivity.this, "Email Send", Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
                            LoginActivity();
                        }
                        else{
                            Toast.makeText(ForgetPasswordActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
                        }
                    }
                });
    }

    private void LoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
}
