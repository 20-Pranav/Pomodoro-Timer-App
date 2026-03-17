package com.pranavbarbade.pomodorotimer;

public class Session {
    private int id;
    private String date;
    private String time;
    private int duration;
    private String purpose;

    // Constructor
    public Session(int id, String date, String time, int duration, String purpose) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.purpose = purpose;
    }

    // Getters
    public int getId() { return id; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public int getDuration() { return duration; }
    public String getPurpose() { return purpose; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setDuration(int duration) { this.duration = duration; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
}
