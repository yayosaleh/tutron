package com.example.tutron;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Date;

public class Lesson implements Parcelable, Identifiable {
    private String id;
    private String tutorId;
    private String studentId;
    private String topicId;
    private String timeslotId;
    private int status = 0; // 0 by default indicating pending status

    // For display purposes
    private String tutorName;
    private String studentName;
    private String topicName;
    private Date startTime;
    private Date endTime;

    // Constructors

    public Lesson() {
        // Public no arg constructor needed to create Firestore documents
    }

    public Lesson(String id, String tutorId, String studentId, String topicId, String timeslotId, String tutorName, String studentName, String topicName, Date startTime, Date endTime) {
        this.id = id;
        this.tutorId = tutorId;
        this.studentId = studentId;
        this.topicId = topicId;
        this.timeslotId = timeslotId;
        this.tutorName = tutorName;
        this.studentName = studentName;
        this.topicName = topicName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and setters


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

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTimeslotId() {
        return timeslotId;
    }

    public void setTimeslotId(String timeslotId) {
        this.timeslotId = timeslotId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
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

    // Parcelable methods
    protected Lesson(Parcel in) {
        id = in.readString();
        tutorId = in.readString();
        studentId = in.readString();
        topicId = in.readString();
        timeslotId = in.readString();
        status = in.readInt();
        tutorName = in.readString();
        studentName = in.readString();
        topicName = in.readString();
    }

    public static final Creator<Lesson> CREATOR = new Creator<Lesson>() {
        @Override
        public Lesson createFromParcel(Parcel in) {
            return new Lesson(in);
        }

        @Override
        public Lesson[] newArray(int size) {
            return new Lesson[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(tutorId);
        dest.writeString(studentId);
        dest.writeString(topicId);
        dest.writeString(timeslotId);
        dest.writeInt(status);
        dest.writeString(tutorName);
        dest.writeString(studentName);
        dest.writeString(topicName);
    }

    // Item view binder
    public void bindToView(View view) {
        // Get text views
        TextView textViewStatus = view.findViewById(R.id.textViewLessonItemStatus);
        TextView textViewTutorName = view.findViewById(R.id.textViewLessonItemTutorName);
        TextView textViewStudentName = view.findViewById(R.id.textViewLessonItemStudentName);
        TextView textViewTopicName = view.findViewById(R.id.textViewLessonItemTopicName);
        TextView textViewTime = view.findViewById(R.id.textViewLessonItemTime);

        // Bind text views
        if (status == -1) {
            textViewStatus.setText("REJECTED");
            textViewStatus.setTextColor(Color.RED);
        } else if (status == 0) {
            textViewStatus.setText("PENDING");
            textViewStatus.setTextColor(Color.GRAY);
        } else if (status == 1) {
            textViewStatus.setText("ACCEPTED");
            textViewStatus.setTextColor(Color.GREEN);
        }

        if (tutorName.isEmpty()) textViewTutorName.setVisibility(View.GONE);
        else textViewTutorName.setText("Tutor: " + tutorName);

        if (studentName.isEmpty()) textViewStudentName.setVisibility(View.GONE);
        else textViewStudentName.setText("Student: " + studentName);

        if (topicName.isEmpty()) textViewTopicName.setVisibility(View.GONE);
        else textViewTopicName.setText("Topic: " + topicName);

        Timeslot timeslot = new Timeslot(null, null, startTime, endTime);
        textViewTime.setText("Time: " + timeslot.toString());
    }
}