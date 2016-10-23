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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button btnLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogOut = (Button) findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    Toast.makeText(MainActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                    Log.d("Authentication",  "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    StartLogin();
                    Log.d("Authentication", "onAuthStateChanged:signed_out");

                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        if(v == btnLogOut)
        {
            logout();
        }
    }

    private void logout() {
        firebaseAuth.signOut();
        Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    void StartLogin(){
        Intent i = new Intent(this, Login.class);
        startActivity(i);
    }


//    private void forgetPassword() {
//        String email = txtEmail.getText().toString().trim();
//        if(TextUtils.isEmpty(email)){
//            //
//            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        progressDialog.setMessage("Registering user...");
//        progressDialog.show();
//
//        firebaseAuth.sendPasswordResetEmail(email)
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()){
//                            Toast.makeText(MainActivity.this, "Email Send", Toast.LENGTH_SHORT).show();
//                            progressDialog.hide();
//                        }
//                        else{
//                            Toast.makeText(MainActivity.this, "Try again", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    public static FirebaseAuth getFirebaseAuth(){
        return firebaseAuth;
    }
}
