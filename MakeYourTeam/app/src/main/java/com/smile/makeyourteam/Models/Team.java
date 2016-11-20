package com.smile.makeyourteam.Models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mp_ng on 11/15/2016.
 */

public class Team implements Serializable{
    public String id;
    public String teamName;

    public Team() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Team(String id, String teamName) {
        this.teamName = teamName;
        this.id = id;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("teamName", teamName);
        return result;
    }
}
