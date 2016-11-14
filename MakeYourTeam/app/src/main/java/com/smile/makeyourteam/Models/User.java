package com.smile.makeyourteam.Models;

/**
 * Created by mpnguyen on 10/11/2016.
 */

public class User {
    public String id;
    public String displayName;
    public String nickName = "";
    public String email;
    public int thumbnail;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String id, String displayName, String email) {
        this.displayName = displayName;
        this.email = email;
        this.id = id;
    }

    public User(String id, String displayName, String email, String nickName, int thumbnail) {
        this.displayName = displayName;
        this.nickName = nickName;
        this.email = email;
        this.id = id;
        this.thumbnail = thumbnail;
    }
}
