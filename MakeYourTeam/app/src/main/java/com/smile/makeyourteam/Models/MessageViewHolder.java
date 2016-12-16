package com.smile.makeyourteam.Models;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.smile.makeyourteam.Activities.ChatActivity;
import com.smile.makeyourteam.Activities.ImageChatActivity;
import com.smile.makeyourteam.R;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by NgoChiHai on 10/27/16.
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {
    public TextView tvMessage, tvDisplayName;
    public CircleImageView avatar;
    public LinearLayout layoutChat, layoutUsername;
    public ImageView ivMessage;
    public ProgressBar progressBar;
    public String imageLink;

    public MessageViewHolder(final View v) {
        super(v);
        tvMessage = (TextView) itemView.findViewById(R.id.chatMessage);
        tvDisplayName = (TextView) itemView.findViewById(R.id.username);
        avatar = (CircleImageView) itemView.findViewById(R.id.avatar);
        layoutChat = (LinearLayout) itemView.findViewById(R.id.chat_layout);
        layoutUsername = (LinearLayout) itemView.findViewById(R.id.username_layout);
        ivMessage = (ImageView) itemView.findViewById(R.id.chatImage);
        ivMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(view.getContext(), "clicked " + imageLink, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(view.getContext(), ImageChatActivity.class);
                i.putExtra("ImageLink", imageLink);
                view.getContext().startActivity(i);
            }
        });
        progressBar = (ProgressBar) itemView.findViewById(R.id.progressBarLoadImage);
    }
}
