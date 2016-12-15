package com.smile.makeyourteam.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.smile.makeyourteam.R;

public class ImageChatActivity extends AppCompatActivity {
    ImageView ivChat;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ivChat = (ImageView) findViewById(R.id.iv_chat);
        if(getIntent().hasExtra("byteArray")) {
            byte[] bytes = getIntent().getByteArrayExtra("byteArray");
            Bitmap _bitmap = BitmapFactory.decodeByteArray(
                    bytes,0,bytes.length);
            ivChat.setImageBitmap(_bitmap);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
