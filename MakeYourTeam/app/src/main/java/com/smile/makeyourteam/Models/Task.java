package com.smile.makeyourteam.Models;

/**
 * Created by mp_ng on 11/20/2016.
 */

public class Task {
    public String id;
    public String title;
    public String asignTo;
    public int estimate;
    public int remaining;
    public int completed;

    Task() {}

    public Task(String id, String title, String asignTo, int estimate, int remaining, int completed) {
        this.id = id;
        this.title = title;
        this.asignTo = asignTo;
        this.estimate = estimate;
        this.remaining = remaining;

    }
}
