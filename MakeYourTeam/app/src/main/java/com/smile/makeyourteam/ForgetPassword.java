package com.smile.makeyourteam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ForgetPassword extends AppCompatActivity implements View.OnClickListener{
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
            //
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Sending email...");
        progressDialog.show();

        MainActivity.getFirebaseAuth().sendPasswordResetEmail(email)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ForgetPassword.this, "Email Send", Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
                            LoginActivity();
                        }
                        else{
                            Toast.makeText(ForgetPassword.this, "Try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void LoginActivity() {
        Intent i = new Intent(this, Login.class);
        startActivity(i);
    }
}
