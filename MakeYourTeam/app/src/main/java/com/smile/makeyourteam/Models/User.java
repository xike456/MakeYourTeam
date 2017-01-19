package com.smile.makeyourteam.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mpnguyen on 10/11/2016.
 */

public class User {
    public String id;
    public String displayName ="";
    public String nickName = "";
    public String email = "";
    public String teamId = "";
    public boolean isNotify = false;
    public long lastMessageTimeStamp = 0;
    public String lastMessage = "";
    public String thumbnail ="";

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String id, String displayName, String email) {
        this.displayName = displayName;
        this.email = email;
        this.id = id;
    }

    public User (String id, String displayName, String email, String nickName, String thumbnail) {
        this.displayName = displayName;
        this.nickName = nickName;
        this.email = email;
        this.id = id;
        this.thumbnail = thumbnail;
    }
}
