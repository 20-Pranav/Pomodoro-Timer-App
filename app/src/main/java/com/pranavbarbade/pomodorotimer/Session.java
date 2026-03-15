package com.pranavbarbade.pomodorotimer;

public class Session {
    private int id;
    private String date;
    private String time;
    private int duration;

    public Session(int id, String date, String time, int duration) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.duration = duration;
    }

    // Getters
    public int getId() { return id; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public int getDuration() { return duration; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setDuration(int duration) { this.duration = duration; }
}
