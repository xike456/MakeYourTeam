package com.smile.makeyourteam.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.smile.makeyourteam.Activities.ChatActivity;
import com.smile.makeyourteam.Activities.MainActivity;
import com.smile.makeyourteam.Config;
import com.smile.makeyourteam.Models.Group;
import com.smile.makeyourteam.Models.User;
import com.smile.makeyourteam.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by mpnguyen on 09/11/2016.
 */

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.MyViewHolder> {

    private Context mContext;
    private List<Group> groups;

    public GroupsAdapter(Context mContext, List<Group> groups) {
        this.mContext = mContext;
        this.groups = groups;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_chat, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.title.setText(group.title);
        holder.timeStamp.setText(timestampToHour(group.timestamp));

        // loading album cover using Glide library
        if (group.thumbnail.length() == 0) {
            holder.thumbnail.setImageDrawable(ContextCompat.getDrawable(mContext,
                    R.drawable.ic_people_black_48dp));
        } else {
            Glide.with(mContext)
                    .load(group.thumbnail)
                    .into(holder.thumbnail);
        }

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });

    }

    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_group_chat_card, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.change_avatar_group:
                    return true;
                case R.id.leave_group:
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, timeStamp;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.txtTitle);
            timeStamp = (TextView) view.findViewById(R.id.txtTimeStamp);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            thumbnail.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context context = itemView.getContext();
            final User finalCurrentUser = MainActivity.currentUser;
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra(Config.ID_GROUP, groups.get(getLayoutPosition()).id);
            intent.putExtra(Config.NAME_GROUP, groups.get(getLayoutPosition()).title);
            intent.putExtra(Config.USER_NAME, getUserName(finalCurrentUser));
            intent.putExtra(Config.PHOTO_URL, finalCurrentUser.thumbnail);
            context.startActivity(intent);
        }

        private String getUserName(User user) {
            if(!user.displayName.isEmpty())
                return user.displayName;
            if(!user.nickName.isEmpty())
                return user.nickName;

            return user.email;
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
}
