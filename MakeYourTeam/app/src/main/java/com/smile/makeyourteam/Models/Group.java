package com.smile.makeyourteam.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mpnguyen on 09/11/2016.
 */

public class Group {
    public String id = "";
    public String title = "";
    public long timestamp;
    public boolean isNotify = false;
    public String thumbnail = "";

    public Group () { }

    public Group(String id, String title, long timeStamp, String thumbnail) {
        this.id = id;
        this.title = title;
        this.timestamp = timeStamp;
        this.thumbnail = thumbnail;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("title", title);
        result.put("timestamp", timestamp);
        result.put("thumbnail", thumbnail);
        return result;
    }
}
