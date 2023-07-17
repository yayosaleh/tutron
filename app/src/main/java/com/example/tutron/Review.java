package com.example.tutron;

import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Review implements Identifiable{
    private String id;
    private String studentId;
    private String tutorId;
    private String topicId;
    private String studentName;
    private String topicName;
    private String body;
    private Date creationDate;
    private double rating;
    private Boolean anon;

    public Review() {

    }

    public Review(String id, String studentId, String tutorId, String topicId, String studentName, String topicName, String body, Date creationDate, double rating, Boolean anon) {
        this.id = id;
        this.studentId = studentId;
        this.tutorId = tutorId;
        this.topicId = topicId;
        this.studentName = studentName;
        this.topicName = topicName;
        this.body = body;
        this.creationDate = creationDate;
        this.rating = rating;
        this.anon = anon;
    }

    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Boolean getAnon() {
        return anon;
    }

    public void setAnon(Boolean anon) {
        this.anon = anon;
    }

    // View binder
    public void bindToView(View view) {
        TextView textViewStudentName = view.findViewById(R.id.textViewReviewItemStudentName);
        TextView textViewCreationDate = view.findViewById(R.id.textViewReviewItemCreationDate);
        TextView textViewTopicName = view.findViewById(R.id.textViewReviewItemTopicName);
        TextView textViewRating = view.findViewById(R.id.textViewReviewItemRating);
        TextView textViewBody = view.findViewById(R.id.textViewReviewItemBody);

        // if anonymous, set student name as Anonymous
        if (anon) {
            textViewStudentName.setText("Anonymous");
        } else {
            textViewStudentName.setText(studentName);
        }

        // convert creation date to string and set it
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        textViewCreationDate.setText(sdf.format(creationDate));

        // set topic name
        textViewTopicName.setText(TopicManagerActivity.capitalizeFirstLetterOfEachWord(topicName));

        // set rating with a star prefix
        String ratingString = "\u2B50 " + String.valueOf(rating);
        textViewRating.setText(ratingString);

        // set review body
        textViewBody.setText(body);
    }
}
