package com.smile.makeyourteam.server;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by NgoChiHai on 10/26/16.
 */

public class Firebase {
    public static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static final FirebaseStorage storage = FirebaseStorage.getInstance();
    public static final StorageReference storageRef = storage.getReferenceFromUrl("gs://makeyourteam-d3cde.appspot.com");
}
