/* User superclass (extended by Student, Tutor & Administrator) */

package com.example.tutron;

public class User {
    private String firstName;
    private String lastName;

    // Constructors

    public User() {
        //public no-arg constructor needed to create Firestore documents
    }

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getter and setter methods

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
