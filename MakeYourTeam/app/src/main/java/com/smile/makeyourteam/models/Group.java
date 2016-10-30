package com.smile.makeyourteam.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mp_ng on 10/30/2016.
 */

public class Group {
    public String id;
    public String groupName;


    public Group() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Group(String id, String displayname) {
        this.groupName = displayname;
        this.id = id;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("groupName", groupName);
        return result;
    }
}
