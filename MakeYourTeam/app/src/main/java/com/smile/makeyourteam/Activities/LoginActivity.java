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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smile.makeyourteam.Models.User;
import com.smile.makeyourteam.R;
import com.smile.makeyourteam.server.Firebase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private Button btnLogin;
    private TextView btnRegister;
    private TextView btnForget;
    private EditText txtEmail;
    private EditText txtPassword;

    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;
    private static final int RC_SIGN_IN = 9001;
    private SignInButton mSignInButton;
    private GoogleApiClient mGoogleApiClient;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // init facebok
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(LoginActivity.this);

        mSignInButton = (SignInButton) findViewById(R.id.login_google_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInGoogle();
            }
        });

        // facebook
        fbLoginButton = (LoginButton) findViewById(R.id.login_facebook_button);
        fbLoginButton.setReadPermissions("email","public_profile");
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progressDialog.show();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (TextView) findViewById(R.id.txtRegister);
        btnForget = (TextView) findViewById(R.id.txtForgetPass);
        txtEmail = (EditText) findViewById(R.id.editEmailLogin);
        txtPassword = (EditText) findViewById(R.id.editPasswordLogin);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnForget.setOnClickListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                progressDialog.show();
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed
                Toast.makeText(LoginActivity.this, "Logged in by Google fail", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.

        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        if (view == btnLogin) {
            Login();
        } else if (view == btnRegister) {
            startRegisterAccount();
        } else if (view == btnForget) {
            startForgetPass();
        }
    }

    private void startForgetPass() {
        Intent i = new Intent(this, ForgetPasswordActivity.class);
        startActivity(i);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        Firebase.firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Toast.makeText(Login.this, "Logged in by Facebook successfull", Toast.LENGTH_SHORT).show();
                        final FirebaseUser user = Firebase.firebaseAuth.getCurrentUser();
                        final DatabaseReference myRef = Firebase.database.getReference("users");

                        myRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (!snapshot.exists()) {
                                    User userData = new User(user.getUid(), user.getDisplayName(), "","",user.getPhotoUrl().toString());
                                    myRef.child(user.getUid()).setValue(userData);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError firebaseError) { }
                        });
                        progressDialog.dismiss();
                        startMainActivity();
                        if (!task.isSuccessful()) {
                           // Toast.makeText(LoginActivity.this, "Logged in by Facebook fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

        Firebase.firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
                            startMainActivity();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "log in fail", Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        Firebase.firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {

                        } else {
                            final FirebaseUser user = Firebase.firebaseAuth.getCurrentUser();
                            final DatabaseReference myRef = Firebase.database.getReference("users");

                            myRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    if (!snapshot.exists()) {
                                        User userData = new User(user.getUid(), user.getDisplayName(), user.getEmail(),"",user.getPhotoUrl().toString());

                                        myRef.child(user.getUid()).setValue(userData);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError firebaseError) { }
                            });

                            progressDialog.dismiss();
                            startMainActivity();
                        }
                    }
                });
    }

    private void startMainActivity() {
        Intent callMain = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(callMain);
        finish();
    }

    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    void startRegisterAccount() {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
        finish();
    }

}
