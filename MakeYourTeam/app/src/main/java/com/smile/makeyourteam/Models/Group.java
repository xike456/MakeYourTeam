package com.smile.makeyourteam.Models;

/**
 * Created by mpnguyen on 09/11/2016.
 */

public class Group {
    private String title;
    private String timeStamp;
    private String members;
    private int thumbnail;

    public Group(String title, String timeStamp, String members, int thumbnail) {
        this.title = title;
        this.timeStamp = timeStamp;
        this.members = members;
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public String getMembers() {
        return members;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
