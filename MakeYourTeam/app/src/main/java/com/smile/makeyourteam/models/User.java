package com.smile.makeyourteam.models;

/**
 * Created by NgoChiHai on 10/26/16.
 */

public class User {
    public String id;
    public String displayname;
    public String email;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String id, String displayname, String email) {
        this.displayname = displayname;
        this.email = email;
        this.id = id;
    }
}
