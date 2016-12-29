package com.smile.makeyourteam.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mp_ng on 11/20/2016.
 */

public class Task implements Parcelable {
    public String id;
    public String title;
    public String assignTo;
    public String assignToId;
    public String state;
    public int estimate;
    public int remaining;
    public int completed;

    Task() {}

    public Task(String id, String title, String assignTo, String assignToId, String state, int estimate, int remaining, int completed) {
        this.id = id;
        this.title = title;
        this.assignTo = assignTo;
        this.assignToId = assignToId;
        this.state = state;
        this.estimate = estimate;
        this.remaining = remaining;
        this.completed = completed;
    }

    protected Task(Parcel in) {
        id = in.readString();
        title = in.readString();
        assignTo = in.readString();
        assignToId = in.readString();
        state = in.readString();
        estimate = in.readInt();
        remaining = in.readInt();
        completed = in.readInt();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(assignTo);
        parcel.writeString(assignToId);
        parcel.writeString(state);
        parcel.writeInt(estimate);
        parcel.writeInt(remaining);
        parcel.writeInt(completed);
    }
}
