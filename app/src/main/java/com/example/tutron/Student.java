/* Class for student user type */

package com.example.tutron;

public class Student extends User {

    private String address;
    private String creditCardInfo;

    // Constructors

    public Student() {
        //public no-arg constructor needed to create Firestore documents
    }

    public Student(String id, String firstName, String lastName, String address, String creditCardInfo) {
        super(id, firstName, lastName);
        this.address = address;
        this.creditCardInfo = creditCardInfo;
    }

    // Getter and setter methods

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreditCardInfo() {
        return creditCardInfo;
    }

    public void setCreditCardInfo(String creditCardInfo) {
        this.creditCardInfo = creditCardInfo;
    }
}
