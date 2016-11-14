package com.smile.makeyourteam.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smile.makeyourteam.Models.User;
import com.smile.makeyourteam.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mpnguyen on 10/11/2016.
 */

public class UserAdapter extends ArrayAdapter<User> {
    Activity context;
    int resourceId;
    List<User> users = new ArrayList<User>();

    public UserAdapter(Activity context, int resource, List<User> objects) {
        super(context, resource, objects);
        this.context = context;
        resourceId = resource;
        users = objects;
    }

    public View getView(int position, View v, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        v = inflater.inflate(resourceId, null);
        User user = users.get(position);
        final ImageView avatar = (ImageView) v.findViewById(R.id.imageAvatarUser);
        final TextView username = (TextView) v.findViewById(R.id.userDisplayName);
        final TextView nickName = (TextView) v.findViewById(R.id.userNickName);
        username.setText(user.displayName);
        nickName.setText(user.nickName);
        avatar.setImageResource(user.thumbnail);
        return v;
    }
}
