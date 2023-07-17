package com.example.tutron;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    // To string method for display purposes
    // Format: "<day of week>, <month> <day of month>, <start time>-<end time>"
    @NonNull
    public String toString() {
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        SimpleDateFormat monthAndDayFormat = new SimpleDateFormat("MMM d", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma", Locale.getDefault());

        String dayOfWeek = dayOfWeekFormat.format(startTime);
        String monthAndDay = monthAndDayFormat.format(startTime);
        String startTimeFormatted = timeFormat.format(startTime);
        String endTimeFormatted = timeFormat.format(endTime);

        return String.format("%s, %s, %s-%s", dayOfWeek, monthAndDay, startTimeFormatted, endTimeFormatted);
    }
}