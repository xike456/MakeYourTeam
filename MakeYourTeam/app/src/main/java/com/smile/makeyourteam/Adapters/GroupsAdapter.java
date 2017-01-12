package com.smile.makeyourteam.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.database.DatabaseReference;
import com.smile.makeyourteam.Activities.AddMemberActivity;
import com.smile.makeyourteam.Activities.ChatActivity;
import com.smile.makeyourteam.Activities.MainActivity;
import com.smile.makeyourteam.Config;
import com.smile.makeyourteam.Fragments.ChangeTitleGroupDialogFragment;
import com.smile.makeyourteam.Fragments.CreateGroupDialogFragment;
import com.smile.makeyourteam.Models.Group;
import com.smile.makeyourteam.Models.User;
import com.smile.makeyourteam.R;
import com.smile.makeyourteam.server.Firebase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static java.security.AccessController.getContext;


/**
 * Created by mpnguyen on 09/11/2016.
 */

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.MyViewHolder> {

    private Context mContext;
    private List<Group> groups;
    private int pos;
    private ProgressBar progressBar;
    private ImageView imageView;

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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Group group = groups.get(position);
        holder.title.setText(group.title);
        holder.timeStamp.setText(timestampToHour(group.timestamp));

        if (group.isNotify) {
            holder.title.setTypeface(null, Typeface.BOLD);
            holder.timeStamp.setTypeface(null, Typeface.BOLD);
        } else {
            holder.title.setTypeface(null, Typeface.NORMAL);
            holder.timeStamp.setTypeface(null, Typeface.NORMAL);
        }

        // loading album cover using Glide library
        if (group.thumbnail.length() == 0) {
            holder.thumbnail.setImageDrawable(ContextCompat.getDrawable(mContext,
                    R.drawable.ic_people_black_48dp));
        } else {
            holder.progressBarThumbnail.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(group.thumbnail)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            Bitmap bitmapResized = Bitmap.createScaledBitmap(resource,
                                    (int) (resource.getWidth() * 0.5), (int) (resource.getHeight() * 0.5), false);
                            holder.thumbnail.setImageBitmap(bitmapResized);
                            holder.progressBarThumbnail.setVisibility(View.INVISIBLE);
                        }
                    });

//            Glide.with(mContext)
//                    .load(group.thumbnail)
//                    .into(holder.thumbnail);
        }

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos = position;
                progressBar = holder.progressBarThumbnail;
                imageView = holder.thumbnail;
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
                    MainActivity.setThumbnailGroup((AppCompatActivity) mContext,groups.get(pos).id,progressBar,imageView);
                    return true;
                case R.id.leave_group:
                    Firebase.database.getReference("users").child(Firebase.firebaseAuth.getCurrentUser().getUid()).child("groups").child(groups.get(pos).id).removeValue();
                    return true;
                case R.id.add_member:
                    Intent intent = new Intent(mContext, AddMemberActivity.class);
                    intent.putExtra(Config.ID_GROUP, groups.get(pos).id);
                    intent.putExtra(Config.NAME_GROUP, groups.get(pos).title);
                    intent.putExtra(Config.PHOTO_URL,groups.get(pos).thumbnail);
                    mContext.startActivity(intent);
                    return true;
                case R.id.change_title_group:
                    showDialogChangeTitleGroup(groups.get(pos).id);
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, timeStamp;
        public ImageView thumbnail, overflow;
        public ProgressBar progressBarThumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.txtTitle);
            timeStamp = (TextView) view.findViewById(R.id.txtTimeStamp);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            progressBarThumbnail = (ProgressBar) view.findViewById(R.id.progress_bar_thumbnail);

            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = itemView.getContext();
                    final User finalCurrentUser = MainActivity.currentUser;
                    final DatabaseReference database = Firebase.database.getReference("users");
                    database.child(finalCurrentUser.id).child("lastMessagesGroup")
                            .child(groups.get(getLayoutPosition()).id)
                            .child("isNotify").setValue(false);
                    groups.get(getLayoutPosition()).isNotify = false;
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra(Config.ID_GROUP, groups.get(getLayoutPosition()).id);
                    intent.putExtra(Config.NAME_GROUP, groups.get(getLayoutPosition()).title);
                    intent.putExtra(Config.USER_NAME, getUserName(finalCurrentUser));
                    intent.putExtra(Config.PHOTO_URL, finalCurrentUser.thumbnail);
                    context.startActivity(intent);
                }
            });
        }

        private String getUserName(User user) {
            if(!user.displayName.equals(""))
                return user.displayName;
            if(!user.nickName.equals(""))
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

    private void showDialogChangeTitleGroup(String GroupID) {
        FragmentManager fm = ((AppCompatActivity)mContext).getSupportFragmentManager();
        ChangeTitleGroupDialogFragment newFragment = new ChangeTitleGroupDialogFragment();
        newFragment.setGroupID(GroupID);
        newFragment.show(fm, "New group fragment");
    }

}
