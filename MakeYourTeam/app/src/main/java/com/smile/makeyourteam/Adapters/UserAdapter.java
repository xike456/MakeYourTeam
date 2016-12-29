package com.smile.makeyourteam.Adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.smile.makeyourteam.Activities.MainActivity;
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
    public List<Boolean> positionChecked = null;

    public UserAdapter(Activity context, int resource, List<User> objects) {
        super(context, resource, objects);
        this.context = context;
        resourceId = resource;
        users = objects;
        if(this.resourceId == R.layout.member_item){
            positionChecked = new ArrayList<>();
            for(int i = 0; i<users.size();i++){
                positionChecked.add(false);
            }
        }
    }

    public View getView(final int position, View v, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        v = inflater.inflate(resourceId, null);
        User user = users.get(position);
        final ImageView avatar = (ImageView) v.findViewById(R.id.imageAvatarUser);
        final TextView username = (TextView) v.findViewById(R.id.userDisplayName);
        final TextView nickName = (TextView) v.findViewById(R.id.userNickName);
        username.setText(user.displayName);
        nickName.setText(user.nickName);

        if (user.thumbnail.length() == 0) {
            avatar.setImageDrawable(ContextCompat.getDrawable(getContext(),
                    R.drawable.ic_people_black_48dp));
        } else {
            Glide.with(getContext())
                    .load(user.thumbnail)
                    .into(avatar);
        }

        if(positionChecked!=null) {
            final CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkboxAdd);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(((CheckBox)view).isChecked()){
                        positionChecked.set(position,true);
                    }else {
                        positionChecked.set(position,false);
                    }
                }
            });
        }
        return v;
    }
}
