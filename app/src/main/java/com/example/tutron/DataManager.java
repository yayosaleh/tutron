package com.example.tutron;

// Singleton utility class used to access current student or tutor instead of passsing instances via intents

public class DataManager {
    private static DataManager instance = null;
    private Tutor currentTutor;
    private Student currentStudent;

    private DataManager() {}

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    // ONLY use in activity lifecycle methods to avoid race conditions
    public void setCurrentTutor(Tutor tutor) {
        this.currentTutor = tutor;
    }

    public Tutor getCurrentTutor() {
        return currentTutor;
    }

    // ONLY use in activity lifecycle methods to avoid race conditions
    public void setCurrentStudent(Student student) {
        this.currentStudent = student;
    }

    public Student getCurrentStudent() {
        return currentStudent;
    }
}