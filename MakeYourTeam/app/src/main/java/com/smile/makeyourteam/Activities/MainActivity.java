package com.smile.makeyourteam.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smile.makeyourteam.Config;
import com.smile.makeyourteam.Fragments.ChatManagerFragment;
import com.smile.makeyourteam.Fragments.HomeFragment;
import com.smile.makeyourteam.Fragments.SettingsFragment;
import com.smile.makeyourteam.Fragments.TaskManagerFragment;
import com.smile.makeyourteam.Models.User;
import com.smile.makeyourteam.R;
import com.smile.makeyourteam.server.Firebase;
import com.smile.makeyourteam.services.Notifications;

import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyAdapter adapter;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progressDialog;
    public static User currentUser = null;
    public static String teamId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading team...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        // init FacebookSDK
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

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = Firebase.firebaseAuth.getCurrentUser();
                if (user != null) {
                    // LoadUser();
                    // LoadGroups();
                    getCurrentUser();
                    checkTeamExist();

                    // start service notification
                    Intent service = new Intent(MainActivity.this,  Notifications.class);
                    startService(service);

                    Log.d("Authentication",  "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    StartLogin();
                    Log.d("Authentication", "onAuthStateChanged:signed_out");
                }
            }
        };


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        adapter = new MyAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
                tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_black_24dp);
                tabLayout.getTabAt(1).setIcon(R.drawable.ic_event_note_black_24dp);
                tabLayout.getTabAt(2).setIcon(R.drawable.ic_chat_black_24dp);
                tabLayout.getTabAt(3).setIcon(R.drawable.ic_settings_black_24dp);

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setTitle(adapter.getPageTitle(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void getCurrentUser() {
        FirebaseUser mUser = Firebase.firebaseAuth.getCurrentUser();
        DatabaseReference database = Firebase.database.getReference("users");
        database.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkTeamExist() {
        FirebaseUser currentUser = Firebase.firebaseAuth.getCurrentUser();
        DatabaseReference database = Firebase.database.getReference("users")
                .child(currentUser.getUid()).child("teamId");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String team = (String) dataSnapshot.getValue();
                if (team == null || team.isEmpty()) {
                    startJoinTeam();
                } else {
                    teamId = team;
                    TaskManagerFragment.loadTasks();
                }
                progressDialog.hide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void startJoinTeam() {
        Intent startJoinTeamActivity = new Intent(MainActivity.this, JoinTeamActivity.class);
        startActivity(startJoinTeamActivity);
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.logout){
            logOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    // function call activity login
    private void StartLogin() {
        Intent login = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(login);
        finish();
    }

    // function user logout
    private void logOut() {
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

    // class adapter tab botttom
    class MyAdapter extends FragmentPagerAdapter {

        Fragment fragments[] = {
                new HomeFragment(),
                new TaskManagerFragment(),
                new ChatManagerFragment(),
                new SettingsFragment()
        };

        String titles[] = { "Home", "Task", "Chat", "Settings" };


        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
