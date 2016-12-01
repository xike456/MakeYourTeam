package com.smile.makeyourteam.Fragments;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smile.makeyourteam.Models.Team;
import com.smile.makeyourteam.Models.User;
import com.smile.makeyourteam.R;
import com.smile.makeyourteam.server.Firebase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {


    private static final int REQUEST_CHOOSE_AVATAR_SETTINGS = 2;
    private static final int IMAGE_MAX_SIDE_LENGTH = 1280;
    private EditText editDisplayName;
    private EditText editNickname;
    private EditText editEmail;
    private EditText editPIN;
    private EditText editTeamName;
    private FirebaseUser mUser;
    private User dbUser;
    private Team dbTeam;
    private CircleImageView imageViewAvatar;
    private Uri uriImageAvatar;
    private Uri uriImageAvatarStore;
    private ProgressDialog mProgress;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        editDisplayName = (EditText) view.findViewById(R.id.editDisplayNameSettings);
        editNickname = (EditText) view.findViewById(R.id.editNicknameSettings);
        editEmail = (EditText) view.findViewById(R.id.editEmailSettings);

        editTeamName = (EditText) view.findViewById(R.id.editTeamNameSettings);
        editPIN = (EditText) view.findViewById(R.id.editPinTeamSettings);
        imageViewAvatar = (CircleImageView) view.findViewById(R.id.ivAvatarSettings);

        imageViewAvatar.setOnClickListener(this);

        uriImageAvatar = null;
        uriImageAvatarStore = null;
        mProgress = new ProgressDialog(getActivity());

        loadUserInfo();

        return view;
    }

    private void loadUserInfo() {
        mUser = Firebase.firebaseAuth.getCurrentUser();
        if (mUser != null) {
            DatabaseReference databaseUser = Firebase.database.getReference().child("users").child(mUser.getUid());
            databaseUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dbUser = dataSnapshot.getValue(User.class);
                    loadTeam();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void loadTeam() {
        if (dbUser == null) return;
        DatabaseReference databaseUser = Firebase.database.getReference().child("teams").child(dbUser.teamId);
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dbTeam = dataSnapshot.getValue(Team.class);
                setToView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setToView() {
        editDisplayName.setText(dbUser.displayName);
        editNickname.setText(dbUser.nickName);
        editEmail.setText(dbUser.email);
        editTeamName.setText(dbTeam.teamName);
        editPIN.setText(dbTeam.id);
        if (dbUser.thumbnail.length() == 0) {
            imageViewAvatar.setImageDrawable(ContextCompat.getDrawable(getContext(),
                    R.drawable.drawer_top));
        } else {
            Glide.with(getContext())
                    .load(dbUser.thumbnail)
                    .into(imageViewAvatar);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnUpdateProfile:
                updateProfile();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateProfile() {
        if (editDisplayName.getText().length() == 0) {
            editDisplayName.setError("Display name can not empty");
            return;
        }

        if (editEmail.getText().length() == 0) {
            editEmail.setError("Email can not empty!");
            return;
        }

        if (editTeamName.getText().length() == 0) {
            editTeamName.setError("Team name can not empty!");
            return;
        }

        mProgress.setCancelable(false);
        mProgress.setTitle("Update Profile");
        mProgress.show();
        if (uriImageAvatar != null) {
            uploadImage();
        } else {
            setProfile();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == imageViewAvatar) {
            Intent intentPick = new Intent();
            intentPick.setAction(Intent.ACTION_GET_CONTENT);
            intentPick.setType("image/*");
            startActivityForResult(Intent.createChooser(intentPick, "Select Picture"), REQUEST_CHOOSE_AVATAR_SETTINGS);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CHOOSE_AVATAR_SETTINGS && resultCode == getActivity().RESULT_OK)  {
            uriImageAvatar = data.getData();
            Bitmap bmImg = loadSizeLimitedBitmapFromUri(uriImageAvatar, getContext().getContentResolver());
            imageViewAvatar.setImageBitmap(bmImg);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImage() {
        StorageReference riversRef = Firebase.storageRef.child("avatarUser/" + uriImageAvatar.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(uriImageAvatar);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                String e = exception.toString();
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                uriImageAvatarStore = taskSnapshot.getDownloadUrl();
                setProfile();
            }
        });
    }

    private void setProfile() {
        mUser = Firebase.firebaseAuth.getCurrentUser();
        DatabaseReference mDatabase = Firebase.database.getReference();
        if (dbUser == null || mUser == null || mDatabase == null) return;
        mDatabase.child("users").child(mUser.getUid()).child("email").setValue(editEmail.getText().toString());
        mDatabase.child("users").child(mUser.getUid()).child("nickName").setValue(editNickname.getText().toString());
        mDatabase.child("users").child(mUser.getUid()).child("displayName").setValue(editDisplayName.getText().toString());

        UserProfileChangeRequest profileUpdates;

        if (uriImageAvatarStore != null) {
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(editDisplayName.getText().toString())
                    .setPhotoUri(uriImageAvatarStore)
                    .build();
            mDatabase.child("users").child(mUser.getUid()).child("thumbnail").setValue(uriImageAvatarStore.toString());
        } else {
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(editDisplayName.getText().toString())
                    .build();
        }

        mUser.updateEmail(editEmail.getText().toString());
        mUser.updateProfile(profileUpdates);
        mProgress.hide();
    }


    /* Helper */
    public static Bitmap loadSizeLimitedBitmapFromUri(
            Uri imageUri,
            ContentResolver contentResolver) {
        try {
            // Load the image into InputStream.
            InputStream imageInputStream = contentResolver.openInputStream(imageUri);

            // For saving memory, only decode the image meta and get the side length.
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Rect outPadding = new Rect();
            BitmapFactory.decodeStream(imageInputStream, outPadding, options);

            // Calculate shrink rate when loading the image into memory.
            int maxSideLength =
                    options.outWidth > options.outHeight ? options.outWidth : options.outHeight;
            options.inSampleSize = 1;
            options.inSampleSize = calculateSampleSize(maxSideLength, IMAGE_MAX_SIDE_LENGTH);
            options.inJustDecodeBounds = false;
            imageInputStream.close();


            // Load the bitmap and resize it to the expected size length
            imageInputStream = contentResolver.openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageInputStream, outPadding, options);
            maxSideLength = bitmap.getWidth() > bitmap.getHeight()
                    ? bitmap.getWidth(): bitmap.getHeight();
            double ratio = IMAGE_MAX_SIDE_LENGTH / (double) maxSideLength;
            if (ratio < 1) {
                bitmap = Bitmap.createScaledBitmap(
                        bitmap,
                        (int)(bitmap.getWidth() * ratio),
                        (int)(bitmap.getHeight() * ratio),
                        false);
            }

            return rotateBitmap(bitmap, getImageRotationAngle(imageUri, contentResolver));
        } catch (Exception e) {
            return null;
        }
    }

    private static int getImageRotationAngle(
            Uri imageUri, ContentResolver contentResolver) throws IOException {
        int angle = 0;
        Cursor cursor = contentResolver.query(imageUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                angle = cursor.getInt(0);
            }
            cursor.close();
        } else {
            ExifInterface exif = new ExifInterface(imageUri.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    angle = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    angle = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    angle = 90;
                    break;
                default:
                    break;
            }
        }
        return angle;
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int angle) {
        // If the rotate angle is 0, then return the original image, else return the rotated image
        if (angle != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(
                    bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else {
            return bitmap;
        }
    }

    private static int calculateSampleSize(int maxSideLength, int expectedMaxImageSideLength) {
        int inSampleSize = 1;

        while (maxSideLength > 2 * expectedMaxImageSideLength) {
            maxSideLength /= 2;
            inSampleSize *= 2;
        }

        return inSampleSize;
    }
}
