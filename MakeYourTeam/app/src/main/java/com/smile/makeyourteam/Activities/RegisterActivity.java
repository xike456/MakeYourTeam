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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.smile.makeyourteam.Models.User;
import com.smile.makeyourteam.R;
import com.smile.makeyourteam.server.Firebase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtEmail;
    private EditText txtPassword;
    private Button btnRegister;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        progressDialog = new ProgressDialog(this);

        btnRegister.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == btnRegister) {
            registerUser();
        }
    }

    private void registerUser() {
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            //
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            //
            return;
        }
        progressDialog.setMessage("Registering user...");
        progressDialog.show();

        Firebase.firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Registerd", Toast.LENGTH_SHORT).show();
                            progressDialog.hide();

                            FirebaseUser user = Firebase.firebaseAuth.getCurrentUser();
                            DatabaseReference myRef = Firebase.database.getReference("users");

                            User userData = new User(user.getUid(), user.getDisplayName(), user.getEmail(), convertToNickname(user.getEmail()), "");
                            myRef.child(user.getUid()).setValue(userData);

                            startCreateTeamActivity();
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Register fail", Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
                        }
                    }
                });
    }

    private String convertToNickname(String email) {
        int idAt = email.indexOf('@');
        if (idAt >= 0) {
            return email.substring(0, idAt);
        }
        return "";
    }

    private void startCreateTeamActivity() {
        Intent joinTeamActivity = new Intent(this, JoinTeamActivity.class);
        startActivity(joinTeamActivity);
        this.finish();
    }
}
