package com.example.tutron;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Topic implements Identifiable, Parcelable {
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

    protected Topic(Parcel in) {
        id = in.readString();
        tutorId = in.readString();
        name = in.readString();
        yearsOfExperience = in.readInt();
        description = in.readString();
        byte tmpOffered = in.readByte();
        offered = tmpOffered == 0 ? null : tmpOffered == 1;
    }

    public static final Creator<Topic> CREATOR = new Creator<Topic>() {
        @Override
        public Topic createFromParcel(Parcel in) {
            return new Topic(in);
        }

        @Override
        public Topic[] newArray(int size) {
            return new Topic[size];
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
        dest.writeString(name);
        dest.writeInt(yearsOfExperience);
        dest.writeString(description);
        dest.writeByte((byte) (offered == null ? 0 : offered ? 1 : 2));
    }
}
