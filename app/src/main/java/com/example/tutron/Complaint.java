package com.example.tutron;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Complaint implements Parcelable, Identifiable {
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

    protected Complaint(Parcel in) {
        id = in.readString();
        tutorId = in.readString();
        tutorName = in.readString();
        description = in.readString();
    }

    public static final Creator<Complaint> CREATOR = new Creator<Complaint>() {
        @Override
        public Complaint createFromParcel(Parcel in) {
            return new Complaint(in);
        }

        @Override
        public Complaint[] newArray(int size) {
            return new Complaint[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(tutorId);
        dest.writeString(tutorName);
        dest.writeString(description);
    }
}
