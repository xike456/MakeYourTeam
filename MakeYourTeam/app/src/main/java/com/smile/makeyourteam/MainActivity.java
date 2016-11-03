package com.smile.makeyourteam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smile.makeyourteam.models.Group;
import com.smile.makeyourteam.models.User;
import com.smile.makeyourteam.server.Firebase;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button btnLogOut;
    private Button btnCreateGroup;
    private ListView lvUser;
    private ListView lvGroup;
    private List<User> uList = new ArrayList<User>();
    private List<Group> groupList = new ArrayList<Group>();
    ArrayAdapter<String> adapterUser;
    ArrayAdapter<String> adapterGroup;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        setContentView(R.layout.activity_main);
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.smile.makeyourteam",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }
        btnLogOut = (Button) findViewById(R.id.btnLogOut);
        btnCreateGroup = (Button) findViewById(R.id.btnCreateGroup);
        lvUser = (ListView) findViewById(R.id.lvUser);
        lvGroup = (ListView) findViewById(R.id.lvGroup);
        btnLogOut.setOnClickListener(this);
        btnCreateGroup.setOnClickListener(this);

        // firebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = Firebase.firebaseAuth.getCurrentUser();
                if (user != null) {
//                    DatabaseReference myRef = Firebase.database.getReference("onlines");
//
//                    myRef.child(user.getUid()).setValue(user.getEmail());

                    LoadUser();
                    LoadGroups();

                    //Toast.makeText(MainActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
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
        } else if(v == btnCreateGroup)
        {
            CreateGroup();
        }
    }

    private void CreateGroup() {
        Intent intent = new Intent(this, CreateGroupActivity.class);
        startActivity(intent);
    }

    private void logout() {
        // logout firebase
        Firebase.firebaseAuth.signOut();
        // logout facebook
        LoginManager.getInstance().logOut();
        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        Firebase.firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            Firebase.firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    void StartLogin(){
        Intent i = new Intent(this, Login.class);
        startActivity(i);
        finish();
    }

    void LoadUser(){

        DatabaseReference database = Firebase.database.getReference("users");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if(!user.id.contentEquals(Firebase.firebaseAuth.getCurrentUser().getUid())) {
                        uList.add(user);
                    }
                }

                final ArrayList<String> emails = new ArrayList<String>();
                for (int i=0;i<uList.size();i++) {
                    if(uList.get(i).displayname == null){
                        emails.add(uList.get(i).email);
                    }else{
                        emails.add(uList.get(i).displayname);
                    }
                }

                adapterUser = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, emails);

                lvUser.setAdapter(adapterUser);
                lvUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Toast.makeText(MainActivity.this,emails.get(position),Toast.LENGTH_LONG).show();
                        Intent i = new Intent(MainActivity.this,ChatActivity.class);
                        i.putExtra(Config.ID_USER_LIST, uList.get(position).id);
                        i.putExtra(Config.NAME_USER_RECEIVE,emails.get(position));
                        startActivity(i);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void LoadGroups(){
        FirebaseUser user = Firebase.firebaseAuth.getCurrentUser();
        DatabaseReference database = Firebase.database.getReference("users").child(user.getUid()).child("groups");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Group group = ds.getValue(Group.class);
                        groupList.add(group);
                }

                final ArrayList<String> group = new ArrayList<String>();
                for (int i=0;i<groupList.size();i++) {
                    group.add(groupList.get(i).groupName);
                }

                adapterGroup = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, group);

                lvGroup.setAdapter(adapterGroup);
                lvGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Toast.makeText(MainActivity.this,emails.get(position),Toast.LENGTH_LONG).show();
                        Intent i = new Intent(MainActivity.this,ChatActivity.class);
                        i.putExtra(Config.GROUP_INFO, (Serializable) groupList.get(position));
                        i.putExtra(Config.ID_GROUP, groupList.get(position).id);
                        startActivity(i);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.

        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
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

    /*public static FirebaseAuth getFirebaseAuth(){
        return firebaseAuth;
    }*/
}
