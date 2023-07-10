/* Class for tutor user type */

package com.example.tutron;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import java.util.ArrayList;

public class Tutor extends User implements Parcelable {

    public static final String INDEF_SUSPENSION = "INDEF";
    private String educationLevel;
    private String nativeLanguage;
    private String description;
    private String profilePic;
    private String suspensionExpiry; // Should be null by default
    private ArrayList<String> offeredTopicNames; // Should be null by default (always set, never added to or removed from)

    // Constructors

    public Tutor() {
        //public no-arg constructor needed to create Firestore documents
    }

    public Tutor(String id, String firstName, String lastName, String educationLevel, String nativeLanguage, String description, String profilePic) {
        super(id, firstName, lastName);
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

    public String getSuspensionExpiry() {
        return suspensionExpiry;
    }
    public void setSuspensionExpiry(String suspensionExpiry) {
        this.suspensionExpiry = suspensionExpiry;
    }
    public ArrayList<String> getOfferedTopicNames() {
        return offeredTopicNames;
    }
    public void setOfferedTopicNames(ArrayList<String> offeredTopicNames) {
        this.offeredTopicNames = offeredTopicNames;
    }

    // Parcelable constructor and methods

    protected Tutor(Parcel in) {
        id = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        educationLevel = in.readString();
        nativeLanguage = in.readString();
        description = in.readString();
        profilePic = in.readString();
        suspensionExpiry = in.readString();
        offeredTopicNames = in.createStringArrayList();
    }

    public static final Creator<Tutor> CREATOR = new Creator<Tutor>() {
        @Override
        public Tutor createFromParcel(Parcel in) {
            return new Tutor(in);
        }

        @Override
        public Tutor[] newArray(int size) {
            return new Tutor[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(educationLevel);
        dest.writeString(nativeLanguage);
        dest.writeString(description);
        dest.writeString(profilePic);
        dest.writeString(suspensionExpiry);
        dest.writeStringList(offeredTopicNames);
    }
}