package com.smile.makeyourteam.Activities;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smile.makeyourteam.Models.Message;
import com.smile.makeyourteam.Models.MessageViewHolder;
import com.smile.makeyourteam.R;
import com.smile.makeyourteam.server.Firebase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SearchMessageActivity extends AppCompatActivity {

    private RecyclerView rcvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_message);
        rcvMessage = (RecyclerView)findViewById(R.id.rcvMessage);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            String extrastring=intent.getBundleExtra(SearchManager.APP_DATA).getString("messageID");
            final String currentUserID = intent.getBundleExtra(SearchManager.APP_DATA).getString("currentUserID");

            final LinearLayout.LayoutParams paramsMsgRight = new LinearLayout.
                    LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            paramsMsgRight.gravity = Gravity.RIGHT;

            final LinearLayout.LayoutParams paramsMsgLeft = new LinearLayout.
                    LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            paramsMsgLeft.gravity = Gravity.LEFT;

            final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
            mLinearLayoutManager.setStackFromEnd(true);
            final FirebaseRecyclerAdapter<Message, MessageViewHolder> mFirebaseAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                    Message.class,
                    R.layout.search_item,
                    MessageViewHolder.class,
                    Firebase.database.getReference("message").child(extrastring)) {
                @Override
                public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    return super.onCreateViewHolder(parent, viewType);
                }

                @Override
                protected void populateViewHolder(final MessageViewHolder viewHolder, final Message message, int position) {
                    viewHolder.tvMessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Toast.makeText(SearchMessageActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                        }
                    });
                    if (message.senderId.equals(currentUserID)) {
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(0,0,0,0);

                        if (message.messages.equals("...") || !containKey(message.messages, query)) {
                            viewHolder.tvDisplayName.setVisibility(View.GONE);
                            viewHolder.tvMessage.setVisibility(View.GONE);
                            viewHolder.avatar.setVisibility(View.GONE);
                            //viewHolder.avatar.setLayoutParams(lp);
                            viewHolder.ivMessage.setVisibility(View.GONE);
                            viewHolder.progressBar.setVisibility(View.GONE);
                            viewHolder.layoutChat.setVisibility(View.GONE);
                            viewHolder.itemView.setVisibility(View.GONE);
                            viewHolder.layoutUsername.setVisibility(View.GONE);
                            viewHolder.chat.setVisibility(View.GONE);
                        } else {
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
//                            if (!message.messageImage.equals("")) {
//                                viewHolder.tvMessage.setVisibility(View.GONE);
//                                viewHolder.ivMessage.setImageBitmap(null);
//                                viewHolder.ivMessage.setVisibility(View.VISIBLE);
//                                viewHolder.progressBar.setVisibility(View.VISIBLE);
//
//                                // Glide.with(ChatActivity.this).load(message.messageImage).into(viewHolder.ivMessage);
//                                Glide.with(SearchMessageActivity.this)
//                                        .load(message.messageImage)
//                                        .asBitmap()
//                                        .into(new SimpleTarget<Bitmap>() {
//                                            @Override
//                                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                                Bitmap bitmapResized = Bitmap.createScaledBitmap(resource,
//                                                        (int) (resource.getWidth() * 0.8), (int) (resource.getHeight() * 0.8), false);
//                                                viewHolder.ivMessage.setImageBitmap(bitmapResized);
//                                                viewHolder.progressBar.setVisibility(View.GONE);
//                                                viewHolder.imageLink = message.messageImage;
//                                            }
//                                        });
//
////                            Glide.with(ChatActivity.this)
////                                    .load(message.messageImage)
////                                    .into(viewHolder.ivMessage);
//
//                                // viewHolder.progressBar.setVisibility(View.GONE);
////                            viewHolder.ivMessage.setMaxHeight(200);
////                            viewHolder.ivMessage.setMaxWidth(150);
//                            }
                        }
                    } else {
                        if (message.messages.equals("...") || !containKey(message.messages, query)) {
                            //Toast.makeText(ChatActivity.this,"nhan typing",Toast.LENGTH_SHORT).show();
                            viewHolder.tvDisplayName.setVisibility(View.GONE);
                            viewHolder.tvMessage.setVisibility(View.GONE);
                            viewHolder.avatar.setVisibility(View.GONE);
                            viewHolder.ivMessage.setVisibility(View.GONE);
                            viewHolder.progressBar.setVisibility(View.GONE);
                        } else {

                            //Toast.makeText(ChatActivity.this,"khong nhan typing",Toast.LENGTH_SHORT).show();
                            viewHolder.tvDisplayName.setVisibility(View.VISIBLE);
                            viewHolder.tvMessage.setVisibility(View.VISIBLE);
                            viewHolder.avatar.setVisibility(View.VISIBLE);
                            viewHolder.ivMessage.setVisibility(View.GONE);

                            if (message.photoUrl != null && message.photoUrl.length() == 0) {
                                viewHolder.avatar.setImageDrawable(ContextCompat.getDrawable(SearchMessageActivity.this,
                                        R.drawable.ic_people_black_48dp));
                            } else {
                                Glide.with(SearchMessageActivity.this)
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

                            if (!message.messageImage.equals("")) {
                                viewHolder.tvMessage.setVisibility(View.GONE);
                                viewHolder.ivMessage.setImageBitmap(null);
                                viewHolder.ivMessage.setVisibility(View.VISIBLE);
                                viewHolder.progressBar.setVisibility(View.VISIBLE);
                                viewHolder.imageLink = message.messageImage;

                                // Glide.with(ChatActivity.this).load(message.messageImage).into(viewHolder.ivMessage);
                                Glide.with(SearchMessageActivity.this)
                                        .load(message.messageImage)
                                        .asBitmap()
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                                Bitmap bitmapResized = Bitmap.createScaledBitmap(resource,
                                                        (int) (resource.getWidth() * 0.8), (int) (resource.getHeight() * 0.8), false);
                                                viewHolder.ivMessage.setImageBitmap(bitmapResized);
                                                viewHolder.progressBar.setVisibility(View.GONE);
                                                viewHolder.imageLink = message.messageImage;
                                            }
                                        });
//                            Glide.with(ChatActivity.this)
//                                    .load(message.messageImage)
//                                    .into(viewHolder.ivMessage);

                                //  viewHolder.progressBar.setVisibility(View.GONE);
//                            viewHolder.ivMessage.setMaxHeight(200);
//                            viewHolder.ivMessage.setMaxWidth(150);
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
        }
    }

    public String timestampToHour(long timestamp){
        String result = "";
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        sdf.setTimeZone(TimeZone.getDefault());
        result = sdf.format(date);
        return result;
    }

    public boolean containKey(String src, String query) {
        String[] parts = query.split(" ");
        for (String part :
                parts) {
            if (src.contains(part)) {
                return true;
            }
        }
        return false;
    }
}
