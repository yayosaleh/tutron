package com.example.tutron;

public class Complaint {
    private String id;
    private String tutorId;
    private String tutorName;
    private String description;

    public Complaint() {
        // Public no-arg constructor needed to create Firestore documents
    }

    public Complaint(String id, String tutorId, String tutorName, String description) {
        this.id = id;
        this.tutorId = tutorId;
        this.tutorName = tutorName;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
