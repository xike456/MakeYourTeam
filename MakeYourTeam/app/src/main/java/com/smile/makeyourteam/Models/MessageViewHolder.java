package com.smile.makeyourteam.Models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.smile.makeyourteam.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by NgoChiHai on 10/27/16.
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {
    public TextView tvMessage, tvDisplayName, tvTimeSend;
    public CircleImageView avatar;

    public MessageViewHolder(View v) {
        super(v);
        tvMessage = (TextView) itemView.findViewById(R.id.chatMessage);
        tvDisplayName = (TextView) itemView.findViewById(R.id.username);
        tvTimeSend = (TextView) itemView.findViewById(R.id.timeSend);
        avatar = (CircleImageView) itemView.findViewById(R.id.avatar);
    }
}
