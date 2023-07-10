/* User superclass (extended by Student, Tutor & Administrator) */

package com.example.tutron;

public class User implements Identifiable {
    protected String id;
    protected String firstName;
    protected String lastName;

    // Constructors

    public User() {
        // Public no-arg constructor needed to create Firestore documents
    }

    public User(String id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getter and setter methods
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
