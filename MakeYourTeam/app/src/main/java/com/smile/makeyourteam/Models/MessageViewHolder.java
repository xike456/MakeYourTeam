package com.smile.makeyourteam.Models;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import com.volokh.danylo.hashtaghelper.HashTagHelper;

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
    public Boolean isFile;
    public String fileUrl;
    public String currentUserID_Clone;
    public String codeStringMessage;
    private HashTagHelper mTextHashTagHelper;
    public LinearLayout chat;

    public MessageViewHolder(final View v) {
        super(v);
        chat = (LinearLayout) itemView.findViewById(R.id.chat_item_layout);
        tvMessage = (TextView) itemView.findViewById(R.id.chatMessage);
        tvDisplayName = (TextView) itemView.findViewById(R.id.username);
        avatar = (CircleImageView) itemView.findViewById(R.id.avatar);
        layoutChat = (LinearLayout) itemView.findViewById(R.id.chat_layout);
        layoutUsername = (LinearLayout) itemView.findViewById(R.id.username_layout);
        ivMessage = (ImageView) itemView.findViewById(R.id.chatImage);
        ivMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), ImageChatActivity.class);
                i.putExtra("ImageLink", imageLink);
                view.getContext().startActivity(i);
            }
        });
        progressBar = (ProgressBar) itemView.findViewById(R.id.progressBarLoadImage);

        tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFile){
                    Intent dowload = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
                    view.getContext().startActivity(dowload);
                }
            }
        });

        SearchManager searchManager =
                (SearchManager) v.getContext().getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = new SearchView(v.getContext());
        searchView.setSearchableInfo(searchManager.getSearchableInfo(((Activity) v.getContext()).getComponentName()));

        char[] additionalSymbols = new char[]{ '_' ,'$', '.'};
        mTextHashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(v.getContext(), R.color.colorHashTag), new HashTagHelper.OnHashTagClickListener() {
            @Override
            public void onHashTagClicked(String hashTag) {
                Bundle appData = new Bundle();
                appData.putString("messageID", codeStringMessage);
                appData.putString("currentUserID", currentUserID_Clone);
                searchView.setAppSearchData(appData);
                searchView.setQuery("#" + hashTag, true);
            }
        }, additionalSymbols);
        mTextHashTagHelper.handle(tvMessage);
    }
}
