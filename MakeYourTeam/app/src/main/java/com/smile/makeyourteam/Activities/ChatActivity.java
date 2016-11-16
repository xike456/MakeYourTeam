package com.smile.makeyourteam.Activities;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.smile.makeyourteam.Config;
import com.smile.makeyourteam.Models.Message;
import com.smile.makeyourteam.Models.MessageViewHolder;
import com.smile.makeyourteam.R;
import com.smile.makeyourteam.server.Firebase;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // get user receive
        final String id_userReceive;
        Intent i = getIntent();
        id_userReceive = i.getStringExtra(Config.ID_USER_REVEIVE);
        nameUserReceive = i.getStringExtra(Config.NAME_USER_RECEIVE);
        userName = i.getStringExtra(Config.USER_NAME);
        photoUrl = i.getStringExtra(Config.PHOTO_URL);

        // set title
        setTitle(nameUserReceive);
        setContentView(R.layout.activity_chat);

        final String currentUserID = Firebase.firebaseAuth.getCurrentUser().getUid();
        final String sRef = currentUserID + "-" + id_userReceive;

        // create code
        Integer code = 0;
        for (int j = 0;j<sRef.length();j++){
            code += sRef.charAt(j);
        }

        String codeString = code.toString();
        btnSend = (Button) findViewById(R.id.btnSend);

        etMessage = (EditText)findViewById(R.id.etMessage);
        rcvMessage = (RecyclerView)findViewById(R.id.rcvMessage);

        final String[] key = {""};
        final String finalCodeString = codeString;
        final int[] count = {0};

//        etMessage.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if(count[0] == 0){
//                    DatabaseReference databaseRef = Firebase.database.getReference("message");
//                    Message message = new Message(currentUserID, "...",Firebase.firebaseAuth.getCurrentUser().getEmail());
//                    key[0] = databaseRef.child(finalCodeString).push().getKey();
//                    databaseRef.child(finalCodeString).child(key[0]).setValue(message);
//                    count[0]++;
//                }
//            }
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//            }
//        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etMessage.getText().toString().equals("")) {
                    return;
                }
               // Firebase.database.getReference("message").child(finalCodeString).child(key[0]).removeValue();
                DatabaseReference databaseRef = Firebase.database.getReference("message");
                Message message = new Message(userName, new Date().getTime(),etMessage.getText().toString(), currentUserID, id_userReceive, photoUrl, nameUserReceive);
                etMessage.setText("");
                databaseRef.child(finalCodeString).push().setValue(message);
                key[0]="";
                count[0] = 0;
            }
        });

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class,
                R.layout.chat_item,
                MessageViewHolder.class,
                Firebase.database.getReference("message").child(finalCodeString)) {

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message message, int position) {
//                if(message.message.equals("...") && message.senderId == currentUserID){
//                    viewHolder.messengerImageView.setVisibility(View.GONE);
//                    viewHolder.messageTextView.setText("");
//                    viewHolder.messengerTextView.setText("");
//                }else if(message.message.equals("...")&& message.senderId != currentUserID){
//                    viewHolder.messengerImageView.setVisibility(View.GONE);
//                    viewHolder.messageTextView.setText(message.message);
//                    viewHolder.messengerTextView.setText("");
//                }else{
//                    viewHolder.messengerImageView.setVisibility(View.VISIBLE);
//                    viewHolder.messageTextView.setText(message.message);
//                    viewHolder.messengerTextView.setText(message.email);
//                }

                viewHolder.tvDisplayName.setText(message.userName);
                viewHolder.tvTimeSend.setText(timestampToHour(message.timestamp));
                viewHolder.tvMessage.setText(message.messages);

                if (message.photoUrl.length() == 0) {
                    viewHolder.avatar.setImageDrawable(ContextCompat.getDrawable(ChatActivity.this,
                            R.drawable.ic_people_black_48dp));
                } else {
                    Glide.with(ChatActivity.this)
                            .load(message.photoUrl)
                            .into(viewHolder.avatar);
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
