package com.smile.makeyourteam.Activities;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
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

    private Button btnSend;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Notifications.isChatActivityLaunch = true;
        Toast.makeText(this,"chat onCreate",Toast.LENGTH_LONG).show();

        // get user receive
        final String id_userReceive;
        Intent i = getIntent();
        id_userReceive = i.getStringExtra(Config.ID_USER_REVEIVE);
        nameUserReceive = i.getStringExtra(Config.NAME_USER_RECEIVE);
        userName = i.getStringExtra(Config.USER_NAME);
        photoUrl = i.getStringExtra(Config.PHOTO_URL);

        if (nameUserReceive == null) {
            isGroupChat = true;
            idGroup = i.getStringExtra(Config.ID_GROUP);
            nameGroup = i.getStringExtra(Config.NAME_GROUP);
        }

        // set title
        if(isGroupChat) {
            setTitle(nameGroup);
        } else  {
            setTitle(nameUserReceive);
        }

        setContentView(R.layout.activity_chat);

        final String currentUserID = Firebase.firebaseAuth.getCurrentUser().getUid();
        final String sRef = currentUserID + "-" + id_userReceive;

        // create code
        Integer code = 0;
        for (int j = 0;j<sRef.length();j++){
            code += sRef.charAt(j);
        }

        String codeString = code.toString();
        if (isGroupChat) {
            codeString = idGroup;
        }

        btnSend = (Button) findViewById(R.id.btnSend);

        etMessage = (EditText)findViewById(R.id.etMessage);
        rcvMessage = (RecyclerView)findViewById(R.id.rcvMessage);
        tvTyping = (TextView) findViewById(R.id.typing);

        final String[] key = {""};
        final String finalCodeString = codeString;
        final int[] count = {0};

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(count[0] == 0){
                    DatabaseReference databaseRef = Firebase.database.getReference("message");
                    Message message;
                    if(isGroupChat){
                        message = new Message(userName, new Date().getTime(),"...", currentUserID, "", photoUrl, "");
                    }else {
                        message = new Message(userName, new Date().getTime(),"...", currentUserID, id_userReceive, photoUrl, nameUserReceive);
                    }
                    key[0] = databaseRef.child(finalCodeString).push().getKey();
                    databaseRef.child(finalCodeString).child(key[0]).setValue(message);
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
                Firebase.database.getReference("message").child(finalCodeString).child(key[0]).removeValue();
               // tvTyping.setVisibility(View.GONE);
                DatabaseReference databaseRef = Firebase.database.getReference("message");
                Message message;
                if(isGroupChat){
                    message = new Message(userName, new Date().getTime(),etMessage.getText().toString(), currentUserID, "", photoUrl, "");
                }else {
                    message = new Message(userName, new Date().getTime(),etMessage.getText().toString(), currentUserID, id_userReceive, photoUrl, nameUserReceive);
                }
                etMessage.setText("");
                databaseRef.child(finalCodeString).push().setValue(message);

                key[0]="";
                count[0] = 0;
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
            protected void populateViewHolder(MessageViewHolder viewHolder, Message message, int position) {

                if(message.senderId.equals(currentUserID)){
                    if(message.messages.equals("...")){
                        tvTyping.setVisibility(View.GONE);
                       viewHolder.tvDisplayName.setVisibility(View.GONE);
                       viewHolder.tvMessage.setVisibility(View.GONE);
                       viewHolder.avatar.setVisibility(View.GONE);
                    }else {
                        tvTyping.setVisibility(View.GONE);
                     //   Toast.makeText(ChatActivity.this,"khong typing",Toast.LENGTH_SHORT).show();
                        viewHolder.tvDisplayName.setVisibility(View.VISIBLE);
                        viewHolder.tvMessage.setVisibility(View.VISIBLE);
                        viewHolder.avatar.setVisibility(View.VISIBLE);

                        viewHolder.avatar.setImageDrawable(null);
                        viewHolder.avatar.setVisibility(View.INVISIBLE);
                        viewHolder.tvMessage.setBackgroundResource(R.drawable.in_message_bg);
                        viewHolder.layoutUsername.setLayoutParams(paramsMsgRight);
                        viewHolder.layoutChat.setLayoutParams(paramsMsgRight);

                        viewHolder.tvDisplayName.setText(message.userName + "  " + timestampToHour(message.timestamp));
                        viewHolder.tvMessage.setText(message.messages);
                    }
                }else if(message.receiveId.equals(currentUserID)){
                    if(message.messages.equals("...")){
                        Toast.makeText(ChatActivity.this,"nhan typing",Toast.LENGTH_SHORT).show();
                        tvTyping.setVisibility(View.VISIBLE);
                        viewHolder.tvDisplayName.setVisibility(View.GONE);
                        viewHolder.tvMessage.setVisibility(View.GONE);
                        viewHolder.avatar.setVisibility(View.GONE);
                    }else {
                        tvTyping.setVisibility(View.GONE);
                        Toast.makeText(ChatActivity.this,"khong nhan typing",Toast.LENGTH_SHORT).show();
                        viewHolder.tvDisplayName.setVisibility(View.VISIBLE);
                        viewHolder.tvMessage.setVisibility(View.VISIBLE);
                        viewHolder.avatar.setVisibility(View.VISIBLE);

                        if (message.photoUrl.length() == 0) {
                            viewHolder.avatar.setImageDrawable(ContextCompat.getDrawable(ChatActivity.this,
                                    R.drawable.ic_people_black_48dp));
                        } else {
                            Glide.with(ChatActivity.this)
                                    .load(message.photoUrl)
                                    .into(viewHolder.avatar);
                        }
                        viewHolder.tvMessage.setBackgroundResource(R.drawable.out_message_bg);
                        viewHolder.layoutUsername.setLayoutParams(paramsMsgLeft);
                        viewHolder.layoutChat.setLayoutParams(paramsMsgLeft);

                        viewHolder.tvDisplayName.setText(message.userName + "  " + timestampToHour(message.timestamp));
                        viewHolder.tvMessage.setText(message.messages);
                    }
                }



//                if( message.senderId == currentUserID && message.messages.equals("...")){
//                    Toast.makeText(ChatActivity.this,"minh gui",Toast.LENGTH_SHORT).show();
////                    tvTyping.setVisibility(View.GONE);
////                    viewHolder.tvDisplayName.setVisibility(View.GONE);
////                    viewHolder.tvMessage.setVisibility(View.GONE);
////                    viewHolder.avatar.setVisibility(View.GONE);
//                    viewHolder.setIsRecyclable(false);
//
//                }else if(message.messages.equals("...")&& message.receiveId != currentUserID){
//                    Toast.makeText(ChatActivity.this,"nguoi khac gui typing",Toast.LENGTH_SHORT).show();
//                    tvTyping.setVisibility(View.VISIBLE);
////                    viewHolder.tvDisplayName.setVisibility(View.GONE);
////                    viewHolder.tvMessage.setVisibility(View.GONE);
////                    viewHolder.avatar.setVisibility(View.GONE);
//                    viewHolder.setIsRecyclable(false);
//
//                }else if(!message.messages.equals("...")){
//                    tvTyping.setVisibility(View.GONE);
//                    Toast.makeText(ChatActivity.this,"khong typing",Toast.LENGTH_SHORT).show();
//                    viewHolder.tvDisplayName.setVisibility(View.VISIBLE);
//                    viewHolder.tvMessage.setVisibility(View.VISIBLE);
//                    viewHolder.avatar.setVisibility(View.VISIBLE);
//
//                    if(message.senderId.equals(currentUserID)){
//                        viewHolder.avatar.setImageDrawable(null);
//                        viewHolder.avatar.setVisibility(View.INVISIBLE);
//                        viewHolder.tvMessage.setBackgroundResource(R.drawable.in_message_bg);
//                        viewHolder.layoutUsername.setLayoutParams(paramsMsgRight);
//                        viewHolder.layoutChat.setLayoutParams(paramsMsgRight);
//                    }else {
//                        if (message.photoUrl.length() == 0) {
//                            viewHolder.avatar.setImageDrawable(ContextCompat.getDrawable(ChatActivity.this,
//                                    R.drawable.ic_people_black_48dp));
//                        } else {
//                            Glide.with(ChatActivity.this)
//                                    .load(message.photoUrl)
//                                    .into(viewHolder.avatar);
//                        }
//                        viewHolder.tvMessage.setBackgroundResource(R.drawable.out_message_bg);
//                        viewHolder.layoutUsername.setLayoutParams(paramsMsgLeft);
//                        viewHolder.layoutChat.setLayoutParams(paramsMsgLeft);
//                    }
//
//
//                    viewHolder.tvDisplayName.setText(message.userName + "  " + timestampToHour(message.timestamp));
//                    viewHolder.tvMessage.setText(message.messages);
//                }
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this,"chat ondestroy",Toast.LENGTH_LONG).show();

        Notifications.isChatActivityLaunch = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this,"chat onpause",Toast.LENGTH_LONG).show();
        Notifications.isChatActivityLaunch = false;
    }

    public String timestampToHour(long timestamp){
        String result = "";
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        sdf.setTimeZone(TimeZone.getDefault());
        result = sdf.format(date);
        return result;
    }
}
