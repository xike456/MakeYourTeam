package com.smile.makeyourteam.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smile.makeyourteam.Models.Message;
import com.smile.makeyourteam.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by mpnguyen on 10/11/2016.
 */

public class ChatAdapter extends ArrayAdapter<Message> {
    Activity c;
    int resourceId;
    List<Message> messages = new ArrayList<Message>();

    public ChatAdapter(Activity context, int resource, List<Message> objects) {
        super(context, resource, objects);
        c = context;
        resourceId = resource;
        messages = objects;
    }

    public View getView(int position, View v, ViewGroup parent){
        LayoutInflater inflater = c.getLayoutInflater();
        v = inflater.inflate(resourceId, null);
        Message chat = messages.get(position);
        final TextView username = (TextView) v.findViewById(R.id.username);
        username.setText(chat.userName);
        final TextView time = (TextView) v.findViewById(R.id.timeSend);
        time.setText(timestampToHour(chat.timestamp));
        final TextView chatMessage = (TextView) v.findViewById(R.id.chatMessage);
        chatMessage.setText(chat.messages);

        return v;
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

