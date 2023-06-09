/* Class for tutor user type */

package com.example.tutron;

public class Tutor extends User {
    private String educationLevel;
    private String nativeLanguage;
    private String description;
    private String profilePic;

    // Constructors

    public Tutor() {
        //public no-arg constructor needed to create Firestore documents
    }

    public Tutor(String firstName, String lastName, String educationLevel, String nativeLanguage, String description, String profilePic) {
        super(firstName, lastName);
        this.educationLevel = educationLevel;
        this.nativeLanguage = nativeLanguage;
        this.description = description;
        this.profilePic = profilePic;
    }

    // Getter and setter methods

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getNativeLanguage() {
        return nativeLanguage;
    }

    public void setNativeLanguage(String nativeLanguage) {
        this.nativeLanguage = nativeLanguage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}