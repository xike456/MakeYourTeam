package com.smile.makeyourteam;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.smile.makeyourteam.models.User;
import com.smile.makeyourteam.server.Firebase;

public class Register extends AppCompatActivity implements View.OnClickListener{

    private EditText txtEmail;
    private EditText txtPassword;
    private Button btnRegister;
    private TextView txtLogin;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        txtEmail = (EditText) findViewById(R.id.editEmail);
        txtPassword = (EditText) findViewById(R.id.editPassword);
        txtLogin = (TextView) findViewById(R.id.txtLoginFromRegister);

        progressDialog = new ProgressDialog(this);

        btnRegister.setOnClickListener(this);
        txtLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnRegister) {
            registerUser();
        }
        if(v== txtLogin){
            LoginActivity();
        }
    }

    private void LoginActivity() {
        Intent i = new Intent(this, Login.class);
        startActivity(i);
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
                            Toast.makeText(Register.this, "Registerd", Toast.LENGTH_SHORT).show();
                            progressDialog.hide();

                            FirebaseUser user = Firebase.firebaseAuth.getCurrentUser();
                            DatabaseReference myRef = Firebase.database.getReference("users");

                            User userData = new User(user.getUid(), user.getDisplayName(), user.getEmail());
                            myRef.child(user.getUid()).setValue(userData);

                            StartMainActivity();
                        }
                        else{
                            Toast.makeText(Register.this, "Register fail", Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
                        }
                    }
                });
    }

    void StartMainActivity(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
