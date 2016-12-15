package com.smile.makeyourteam.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smile.makeyourteam.Config;
import com.smile.makeyourteam.Models.Message;
import com.smile.makeyourteam.Models.MessageViewHolder;
import com.smile.makeyourteam.R;
import com.smile.makeyourteam.server.Firebase;
import com.smile.makeyourteam.services.Notifications;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ChatActivity extends AppCompatActivity {

    private Button btnSend, btnSelecteImage;
    private RecyclerView rcvMessage;
    private EditText etMessage;

    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<Message, MessageViewHolder> mFirebaseAdapter;
    private String nameUserReceive;
    private String userName;
    private String photoUrl;
    private boolean isGroupChat = false;
    private String idGroup;
    private String nameGroup;
    public static TextView tvTyping;

    final String[] key = {""};
    final int[] count = {0};
    private String finalCodeStringUseClear;
    private String currentUserIdReset;

    private static String messageID;
    public static int REQUEST_CHOOSE_IMAGE = 2;
    public static String currentUserID_Clone;
    public static String id_userReceive_Clone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Notifications.isAppFocus = true;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        // Notifications.isChatActivityLaunch = true;
      //  Toast.makeText(this,"chat onCreate",Toast.LENGTH_LONG).show();

        // get user receive
        final String id_userReceive;
        Intent i = getIntent();
        id_userReceive = i.getStringExtra(Config.ID_USER_REVEIVE);
        id_userReceive_Clone = id_userReceive;
        nameUserReceive = i.getStringExtra(Config.NAME_USER_RECEIVE);
        userName = i.getStringExtra(Config.USER_NAME);
        photoUrl = i.getStringExtra(Config.PHOTO_URL);

        Notifications.idGroupPerson = nameUserReceive;

        if (nameUserReceive == null) {
            isGroupChat = true;
            idGroup = i.getStringExtra(Config.ID_GROUP);
            nameGroup = i.getStringExtra(Config.NAME_GROUP);
        }

        // set title
        if(isGroupChat) {
            Notifications.idGroupPerson = nameGroup;
            setTitle(nameGroup);
        } else  {
            setTitle(nameUserReceive);
        }

        setContentView(R.layout.activity_chat);

        final String currentUserID = Firebase.firebaseAuth.getCurrentUser().getUid();
        currentUserIdReset = currentUserID;
        currentUserID_Clone = currentUserID;
        String sRef = currentUserID + id_userReceive;

        // create code
        Integer code = 0;
        for (int j = 0; j < sRef.length(); j++){
            code += sRef.charAt(j);
        }

        String codeString = code.toString();

        if (isGroupChat) {
            codeString = idGroup;
        }

        btnSend = (Button) findViewById(R.id.btnSend);
        btnSelecteImage = (Button) findViewById(R.id.btnSelecteImage);

        etMessage = (EditText)findViewById(R.id.etMessage);
        rcvMessage = (RecyclerView)findViewById(R.id.rcvMessage);
        tvTyping = (TextView) findViewById(R.id.typing);


        final String finalCodeString = codeString;
        finalCodeStringUseClear = codeString;

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(count[0] == 0){

                    DatabaseReference databaseRef = Firebase.database.getReference("typing").child(finalCodeStringUseClear);
                    databaseRef.child(currentUserID).setValue("true");

                    count[0]++;
                }
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etMessage.getText().toString().equals("")) {
                    return;
                }
                DatabaseReference databaseRef = Firebase.database.getReference("message");
                Message message;
                if(isGroupChat){
                    message = new Message(userName, new Date().getTime(),etMessage.getText().toString(), currentUserID, idGroup, photoUrl, "G-"+nameGroup,"");
                }else {
                    message = new Message(userName, new Date().getTime(),etMessage.getText().toString(), currentUserID, id_userReceive, photoUrl, nameUserReceive,"");
                }
                etMessage.setText("");
                databaseRef.child(finalCodeString).push().setValue(message);

                DatabaseReference databaseRefTyping = Firebase.database.getReference("typing").child(finalCodeStringUseClear);
                databaseRefTyping.child(currentUserID).setValue("false");

                count[0] = 0;
            }
        });

        btnSelecteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setImageSend(ChatActivity.this);
            }
        });

        final LinearLayout.LayoutParams paramsMsgRight = new LinearLayout.
                LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsMsgRight.gravity = Gravity.RIGHT;

        final LinearLayout.LayoutParams paramsMsgLeft = new LinearLayout.
                LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsMsgLeft.gravity = Gravity.LEFT;

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class,
                R.layout.chat_item,
                MessageViewHolder.class,
                Firebase.database.getReference("message").child(finalCodeString)) {
            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return super.onCreateViewHolder(parent, viewType);
            }

            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder, Message message, int position) {

                if(message.senderId.equals(currentUserID)){
                    if(message.messages.equals("...")){
                        viewHolder.tvDisplayName.setVisibility(View.GONE);
                        viewHolder.tvMessage.setVisibility(View.GONE);
                        viewHolder.avatar.setVisibility(View.GONE);
                        viewHolder.ivMessage.setVisibility(View.GONE);
                        viewHolder.progressBar.setVisibility(View.GONE);
                    }else {
                        //   Toast.makeText(ChatActivity.this,"khong typing",Toast.LENGTH_SHORT).show();
                        viewHolder.tvDisplayName.setVisibility(View.VISIBLE);
                        viewHolder.tvMessage.setVisibility(View.VISIBLE);
                        viewHolder.avatar.setVisibility(View.VISIBLE);
                        viewHolder.ivMessage.setVisibility(View.GONE);

                        viewHolder.avatar.setImageDrawable(null);
                        viewHolder.avatar.setVisibility(View.INVISIBLE);
                        viewHolder.tvMessage.setBackgroundResource(R.drawable.in_message_bg);
                        viewHolder.ivMessage.setBackgroundResource(R.drawable.in_message_bg);
                        viewHolder.layoutUsername.setLayoutParams(paramsMsgRight);
                        viewHolder.layoutChat.setLayoutParams(paramsMsgRight);

                        viewHolder.tvDisplayName.setText(message.userName + "  " + timestampToHour(message.timestamp));
                        viewHolder.tvMessage.setText(message.messages);

                        viewHolder.progressBar.setVisibility(View.GONE);
                        if(!message.messageImage.equals("")){
                            viewHolder.tvMessage.setVisibility(View.GONE);
                            viewHolder.ivMessage.setImageBitmap(null);
                            viewHolder.ivMessage.setVisibility(View.VISIBLE);
                            viewHolder.progressBar.setVisibility(View.VISIBLE);

                           // Glide.with(ChatActivity.this).load(message.messageImage).into(viewHolder.ivMessage);
                            Glide.with(ChatActivity.this)
                                    .load(message.messageImage)
                                    .asBitmap()
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                            viewHolder.ivMessage.setImageBitmap(resource);
                                            viewHolder.progressBar.setVisibility(View.GONE);
                                        }
                                    });

                           // viewHolder.progressBar.setVisibility(View.GONE);
                            viewHolder.ivMessage.setMaxHeight(200);
                            viewHolder.ivMessage.setMaxWidth(150);
                        }
                    }
                }else{
                    if(message.messages.equals("...")){
                        //Toast.makeText(ChatActivity.this,"nhan typing",Toast.LENGTH_SHORT).show();
                        viewHolder.tvDisplayName.setVisibility(View.GONE);
                        viewHolder.tvMessage.setVisibility(View.GONE);
                        viewHolder.avatar.setVisibility(View.GONE);
                        viewHolder.ivMessage.setVisibility(View.GONE);
                        viewHolder.progressBar.setVisibility(View.GONE);
                    }else {

                        //Toast.makeText(ChatActivity.this,"khong nhan typing",Toast.LENGTH_SHORT).show();
                        viewHolder.tvDisplayName.setVisibility(View.VISIBLE);
                        viewHolder.tvMessage.setVisibility(View.VISIBLE);
                        viewHolder.avatar.setVisibility(View.VISIBLE);
                        viewHolder.ivMessage.setVisibility(View.GONE);

                        if (message.photoUrl != null && message.photoUrl.length() == 0) {
                            viewHolder.avatar.setImageDrawable(ContextCompat.getDrawable(ChatActivity.this,
                                    R.drawable.ic_people_black_48dp));
                        } else {
                            Glide.with(ChatActivity.this)
                                    .load(message.photoUrl)
                                    .into(viewHolder.avatar);
                        }
                        viewHolder.tvMessage.setBackgroundResource(R.drawable.out_message_bg);
                        viewHolder.ivMessage.setBackgroundResource(R.drawable.out_message_bg);
                        viewHolder.layoutUsername.setLayoutParams(paramsMsgLeft);
                        viewHolder.layoutChat.setLayoutParams(paramsMsgLeft);

                        viewHolder.tvDisplayName.setText(message.userName + "  " + timestampToHour(message.timestamp));
                        viewHolder.tvMessage.setText(message.messages);
                        viewHolder.progressBar.setVisibility(View.GONE);

                        if(!message.messageImage.equals("")){
                            viewHolder.tvMessage.setVisibility(View.GONE);
                            viewHolder.ivMessage.setImageBitmap(null);
                            viewHolder.ivMessage.setVisibility(View.VISIBLE);
                            viewHolder.progressBar.setVisibility(View.VISIBLE);
                            viewHolder.imageLink = message.messageImage;

                           // Glide.with(ChatActivity.this).load(message.messageImage).into(viewHolder.ivMessage);
                            Glide.with(ChatActivity.this)
                                    .load(message.messageImage)
                                    .asBitmap()
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                            viewHolder.ivMessage.setImageBitmap(resource);
                                            viewHolder.progressBar.setVisibility(View.GONE);
                                        }
                                    });
                          //  viewHolder.progressBar.setVisibility(View.GONE);
                            viewHolder.ivMessage.setMaxHeight(200);
                            viewHolder.ivMessage.setMaxWidth(150);
                        }
                    }
                }

            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    rcvMessage.scrollToPosition(positionStart);
                }
            }
        });

        rcvMessage.setLayoutManager(mLinearLayoutManager);
        rcvMessage.setAdapter(mFirebaseAdapter);

        DatabaseReference databaseRefTyping = Firebase.database.getReference("typing").child(finalCodeStringUseClear);
        databaseRefTyping.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    String key = ds.getKey();
                    String typing = ds.getValue().toString();
                    if (!key.equals(currentUserID)){
                        if (typing.equals("true")){
                            tvTyping.setVisibility(View.VISIBLE);
                            return;
                        }
                        else {
                            tvTyping.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this,"chat ondestroy",Toast.LENGTH_LONG).show();
        resetTyping();
      //  Notifications.isChatActivityLaunch = false;
        Notifications.isAppFocus = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
       // Toast.makeText(this,"chat onpause",Toast.LENGTH_LONG).show();
        resetTyping();
        Notifications.isAppFocus = false;
        Notifications.idGroupPerson="";
       // Notifications.isChatActivityLaunch = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Notifications.isAppFocus = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CHOOSE_IMAGE && resultCode == RESULT_OK)  {
            Uri uri = data.getData();

            StorageReference riversRef = Firebase.storageRef.child("messageImage/"+uri.getLastPathSegment());
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
                    //changeThumbnailGroup(downloadUrl.toString());
                    DatabaseReference databaseRef = Firebase.database.getReference("message");
                    Message message;
                    if(isGroupChat){
                        message = new Message(userName, new Date().getTime(),etMessage.getText().toString(), currentUserID_Clone, idGroup, photoUrl, "G-"+nameGroup,downloadUrl.toString());
                    }else {
                        message = new Message(userName, new Date().getTime(),etMessage.getText().toString(), currentUserID_Clone, id_userReceive_Clone, photoUrl, nameUserReceive,downloadUrl.toString());
                    }
                    databaseRef.child(finalCodeStringUseClear).push().setValue(message);
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String timestampToHour(long timestamp){
        String result = "";
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        sdf.setTimeZone(TimeZone.getDefault());
        result = sdf.format(date);
        return result;
    }

    public void resetTyping(){
        DatabaseReference databaseRefTyping = Firebase.database.getReference("typing").child(finalCodeStringUseClear);
        databaseRefTyping.child(currentUserIdReset).setValue("false");
        count[0] = 0;
    }


    public static void setImageSend(Activity activity){
        //ChatActivity.messageID = messageID;
        Intent intentPick = new Intent();
        intentPick.setAction(Intent.ACTION_GET_CONTENT);
        intentPick.setType("image/*");
        activity.startActivityForResult(intentPick,REQUEST_CHOOSE_IMAGE);
    }
}
