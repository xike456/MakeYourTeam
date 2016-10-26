package com.smile.makeyourteam.server;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by NgoChiHai on 10/26/16.
 */

public class Firebase {
    public static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
}
