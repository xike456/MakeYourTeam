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
import com.google.firebase.auth.AuthResult;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private EditText txtEmail;
    private EditText txtPassword;
    private Button btnLogin;
    private TextView txtRegister;
    private ProgressDialog progressDialog;
    private TextView txtLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);

        txtEmail = (EditText) findViewById(R.id.editEmailLogin);
        txtPassword = (EditText) findViewById(R.id.editPasswordLogin);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtRegister = (TextView) findViewById(R.id.txtRegister);
        txtLogin = (TextView) findViewById(R.id.txtLogin);

        btnLogin.setOnClickListener(this);
        txtRegister.setOnClickListener(this);
        txtLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==btnLogin){
            Login();
        }
        if(v==txtRegister){
            RegisterNewAccount();
        }
        if(v==txtLogin){
            ForgetPasswordActivity();
        }
    }

    private void ForgetPasswordActivity() {
        Intent i = new Intent(this, ForgetPassword.class);
        startActivity(i);
    }

    private void RegisterNewAccount() {
        StartRegisterAccount();
    }

    private void Login() {
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
        progressDialog.setMessage("Login ...");
        progressDialog.show();

        MainActivity.getFirebaseAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Logged in", Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
                            StartMainActivity();
                        }
                        else{
                            Toast.makeText(Login.this, "log in fail", Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
                        }
                    }
                });
    }

    void StartMainActivity(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    void StartRegisterAccount() {
        Intent i = new Intent(this, Register.class);
        startActivity(i);
    }
}
