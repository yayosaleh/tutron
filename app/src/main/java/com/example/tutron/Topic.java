package com.example.tutron;

public class Topic implements Identifiable {
    private String id;
    private String tutorId;
    private String name;
    private int yearsOfExperience;
    private String description;
    private Boolean offered;

    public Topic() {
        //public no-arg constructor needed to create Firestore documents
    }

    public Topic(String id, String tutorId, String name, int yearsOfExperience, String description, Boolean offered) {
        this.id = id;
        this.tutorId = tutorId;
        this.name = name;
        this.yearsOfExperience = yearsOfExperience;
        this.description = description;
        this.offered = offered;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getOffered() {
        return offered;
    }

    public void setOffered(Boolean offered) {
        this.offered = offered;
    }
}
