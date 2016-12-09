package com.smile.makeyourteam.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyAdapter adapter;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static User currentUser = null;

    public static String teamId = "";
    public static int REQUEST_CHOOSE = 1;
    private static String groupID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Loading team...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();


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
                    Intent service = new Intent(MainActivity.this,  Notifications.class);
                    startService(service);
                    Notifications.isAppFocus = true;

                    Log.d("Authentication",  "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    StartLogin();
                    Log.d("Authentication", "onAuthStateChanged:signed_out");
                }
            }
        };

        verifyStoragePermissions(this);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CHOOSE && resultCode == RESULT_OK)  {
            Uri uri = data.getData();

            StorageReference riversRef = Firebase.storageRef.child("avatarGroup/"+uri.getLastPathSegment());
            UploadTask uploadTask = riversRef.putFile(uri);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    String e = exception.toString();
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    changeThumbnailGroup(downloadUrl.toString());
                }
            });
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void setThumbnailGroup(Activity activity, String groupID){
        MainActivity.groupID = groupID;
        Intent intentPick = new Intent();
        intentPick.setAction(Intent.ACTION_GET_CONTENT);
        intentPick.setType("image/*");
        activity.startActivityForResult(intentPick,REQUEST_CHOOSE);
    }

    public void changeThumbnailGroup(final String url){
        DatabaseReference mDatabase = Firebase.database.getReference();
        FirebaseUser mUser = Firebase.firebaseAuth.getCurrentUser();
        if (mDatabase == null || mUser == null)
            return;

        DatabaseReference database = Firebase.database.getReference("users");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if(ds.child("groups").child(groupID).exists()){
                        DatabaseReference databaseRef = Firebase.database.getReference("users").child(ds.getKey()).child("groups").child(groupID);
                        databaseRef.child("thumbnail").setValue(url);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Notifications.isAppFocus = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Notifications.isAppFocus = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Notifications.isAppFocus = true;
    }
}
