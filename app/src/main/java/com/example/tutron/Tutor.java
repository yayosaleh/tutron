/* Class for tutor user type */

package com.example.tutron;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Tutor extends User implements Parcelable {

    public static final String INDEF_SUSPENSION = "INDEF";
    private static final List<Integer> PROFILE_PICTURES = Arrays.asList(
            R.drawable.albert_einstein,
            R.drawable.marie_curie,
            R.drawable.isaac_newton
    );
    private String educationLevel;
    private String nativeLanguage;
    private String description;
    private int profilePic;
    private String suspensionExpiry; // Should be null by default
    private ArrayList<String> offeredTopicNames; // Should be null by default (always set, never added to or removed from)
    private double hourlyRate = 0; // 0 by default
    private int numLessonsGiven = 0; // 0 by default
    private int numRatings = 0; // 0 by default
    private double avgRating = 0; // 0 by default

    // Constructors

    public Tutor() {
        //public no-arg constructor needed to create Firestore documents
    }

    public Tutor(String id, String firstName, String lastName, String educationLevel, String nativeLanguage, String description, int profilePic) {
        super(id, firstName, lastName);
        this.educationLevel = educationLevel;
        this.nativeLanguage = nativeLanguage;
        this.description = description;

        // Assign random profile picture
        Random random = new Random();
        this.profilePic = PROFILE_PICTURES.get(random.nextInt(PROFILE_PICTURES.size()));
    }

    public Tutor copy() {
        Tutor copy = new Tutor(this.id, this.firstName, this.lastName, this.educationLevel, this.nativeLanguage, this.description, this.profilePic);
        copy.setSuspensionExpiry(this.suspensionExpiry);
        copy.setOfferedTopicNames(this.offeredTopicNames);
        copy.setHourlyRate(this.hourlyRate);
        copy.setNumLessonsGiven(this.numLessonsGiven);
        copy.setNumRatings(this.numRatings);
        copy.setAvgRating(this.avgRating);
        return copy;
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

    public int getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(int profilePic) {
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

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public int getNumLessonsGiven() {
        return numLessonsGiven;
    }

    public void setNumLessonsGiven(int numLessonsGiven) {
        this.numLessonsGiven = numLessonsGiven;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    // Parcelable constructor and methods

    protected Tutor(Parcel in) {
        id = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        educationLevel = in.readString();
        nativeLanguage = in.readString();
        description = in.readString();
        profilePic = in.readInt();
        suspensionExpiry = in.readString();
        offeredTopicNames = in.createStringArrayList();
        hourlyRate = in.readDouble();
        numLessonsGiven = in.readInt();
        numRatings = in.readInt();
        avgRating = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(educationLevel);
        dest.writeString(nativeLanguage);
        dest.writeString(description);
        dest.writeInt(profilePic);
        dest.writeString(suspensionExpiry);
        dest.writeStringList(offeredTopicNames);
        dest.writeDouble(hourlyRate);
        dest.writeInt(numLessonsGiven);
        dest.writeInt(numRatings);
        dest.writeDouble(avgRating);
    }

    @Override
    public int describeContents() {
        return 0;
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

    // Binds current Tutor instance to reusable tutor_item layout view
    public void bindToView(View view) {
        ImageView imageViewTutorItemProfile = view.findViewById(R.id.imageViewTutorItemProfile);
        TextView textViewTutorItemName = view.findViewById(R.id.textViewTutorItemName);
        TextView textViewTutorItemRating = view.findViewById(R.id.textViewTutorItemRating);
        TextView textViewTutorItemEducation = view.findViewById(R.id.textViewTutorItemEducation);
        TextView textViewTutorItemLanguage = view.findViewById(R.id.textViewTutorItemLanguage);
        TextView textViewTutorItemDescription = view.findViewById(R.id.textViewTutorItemDescription);

        imageViewTutorItemProfile.setImageResource(profilePic);
        textViewTutorItemName.setText(firstName + " " + lastName);
        textViewTutorItemRating.setText(String.format("⭐%.1f ⋅ %d lessons given", avgRating, numLessonsGiven));
        textViewTutorItemEducation.setText(String.format("\uD83C\uDF93%s", educationLevel));
        textViewTutorItemLanguage.setText(String.format("\uD83D\uDDE3%s", nativeLanguage));
        textViewTutorItemDescription.setText(description);

        textViewTutorItemName.setVisibility((firstName.isEmpty() || lastName.isEmpty()) ? View.GONE : View.VISIBLE);
        textViewTutorItemRating.setVisibility((avgRating < 0 || numLessonsGiven < 0) ? View.GONE : View.VISIBLE);
        textViewTutorItemEducation.setVisibility(educationLevel.isEmpty() ? View.GONE : View.VISIBLE);
        textViewTutorItemLanguage.setVisibility(nativeLanguage.isEmpty() ? View.GONE : View.VISIBLE);
        textViewTutorItemDescription.setVisibility(description.isEmpty() ? View.GONE : View.VISIBLE);
    }
}