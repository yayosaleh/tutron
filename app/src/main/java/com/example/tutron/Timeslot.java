package com.example.tutron;

import java.util.Date;

public class Timeslot implements Identifiable {
    private String id;
    private String tutorId;
    private Date startTime;
    private Date endTime;

    public Timeslot() {
        // Public no-arg constructor needed to create Firestore documents
    }

    public Timeslot(String id, String tutorId, Date startTime, Date endTime) {
        this.id = id;
        this.tutorId = tutorId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
