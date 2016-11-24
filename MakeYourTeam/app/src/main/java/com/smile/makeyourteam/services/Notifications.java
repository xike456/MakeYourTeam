package com.smile.makeyourteam.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.smile.makeyourteam.Activities.ChatActivity;
import com.smile.makeyourteam.Config;
import com.smile.makeyourteam.Models.Message;
import com.smile.makeyourteam.R;
import com.smile.makeyourteam.server.Firebase;

/**
 * Created by Tan Vu Le on 11/16/2016.
 */

public class Notifications extends Service{

    private String userID;
    private int count;
    public static Boolean isChatActivityLaunch = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "service onCreate", Toast.LENGTH_SHORT).show();
        count = 0;
        userID = Firebase.firebaseAuth.getCurrentUser().getUid();
        isChatActivityLaunch = false;

        DatabaseReference mRef = Firebase.database.getReference().child("message");
        mRef.keepSynced(true);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(Notifications.this, "vao ham onChildChanged-" + isChatActivityLaunch.toString() + userID, Toast.LENGTH_SHORT).show();
                Message message = new Message();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    message = ds.getValue(Message.class);
                }

                if(message.receiveId.equals(Firebase.firebaseAuth.getCurrentUser().getUid()) && isChatActivityLaunch == false){
                    Toast.makeText(Notifications.this,"send noti", Toast.LENGTH_SHORT).show();

                   // sendNotification(message);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mRef.addChildEventListener(childEventListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        userID = Firebase.firebaseAuth.getCurrentUser().getUid();
        isChatActivityLaunch = false;
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "service destroy", Toast.LENGTH_SHORT).show();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendNotification(Message message) {
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra(Config.ID_USER_REVEIVE, message.senderId);
        i.putExtra(Config.NAME_USER_RECEIVE, message.userName);
        i.putExtra(Config.USER_NAME,message.nameUserReceive);
        i.putExtra(Config.PHOTO_URL, Firebase.firebaseAuth.getCurrentUser().getPhotoUrl().toString());
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       // startActivity(i);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, i,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.drawer_top)
                .setContentTitle("MakeYourTeam")
                .setContentText(message.messages)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
