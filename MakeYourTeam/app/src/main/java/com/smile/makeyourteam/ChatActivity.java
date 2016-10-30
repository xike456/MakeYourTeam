package com.smile.makeyourteam;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smile.makeyourteam.models.Message;
import com.smile.makeyourteam.models.MessageViewHolder;
import com.smile.makeyourteam.models.User;
import com.smile.makeyourteam.server.Firebase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    Button btnSend;
    Button btnAdd;
    RecyclerView rcvMessage;
    EditText etMessage;

    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<Message, MessageViewHolder> mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final String id_userReceive;
        String id_Group = null;

        Intent i = getIntent();
        id_userReceive = i.getStringExtra(Config.ID_USER_LIST);

        final String currentUserID = Firebase.firebaseAuth.getCurrentUser().getUid();
        final String sRef = currentUserID + "-" + id_userReceive;
        Integer code = 0;
        for (int j = 0;j<sRef.length();j++){
            code += sRef.charAt(j);
        }
        String codeString = code.toString();
        btnSend = (Button) findViewById(R.id.btnSend);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        etMessage = (EditText)findViewById(R.id.etMessage);
        rcvMessage = (RecyclerView)findViewById(R.id.rcvMessage);

        if (id_userReceive == null) {
            id_Group = i.getStringExtra(Config.ID_GROUP);
            codeString = id_Group;
            btnAdd.setVisibility(View.VISIBLE);
        }

        final String finalCodeString = codeString;
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ChatActivity.this,"Click",Toast.LENGTH_LONG).show();
                DatabaseReference databaseRef = Firebase.database.getReference("message");
                Message message = new Message(currentUserID, etMessage.getText().toString(),Firebase.firebaseAuth.getCurrentUser().getEmail());
                etMessage.setText("");
                databaseRef.child(finalCodeString).push().setValue(message);
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class,
                R.layout.item_message,
                MessageViewHolder.class,
                Firebase.database.getReference("message").child(finalCodeString)) {

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message message, int position) {
                viewHolder.messageTextView.setText(message.message);
                viewHolder.messengerTextView.setText(message.email);
//                if (message.getPhotoUrl() == null) {
//                    viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,
//                            R.drawable.ic_account_circle_black_36dp));
//                } else {
//                    Glide.with(MainActivity.this)
//                            .load(friendlyMessage.getPhotoUrl())
//                            .into(viewHolder.messengerImageView);
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
}
