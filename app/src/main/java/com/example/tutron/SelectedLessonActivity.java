package com.example.tutron;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class SelectedLessonActivity extends AppCompatActivity {
    private static final Class<?> BACK_NAV_DEST = StudentLessonManagerActivity.class;
    private static final String REVIEW_COLLECTION = "reviews";
    private static final String COMPLAINT_COLLECTION = "complaints";

    private Student currentStudent;
    private Lesson selectedLesson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_lesson);

        // Get current student
        currentStudent = DataManager.getInstance().getCurrentStudent();

        // Get lesson sent via intent
        selectedLesson = getIntent().getParcelableExtra("Selected Lesson");

        // Initialize view variables
        Button btnBackNav = findViewById(R.id.btnSelectedLessonBackNav);
        Button btnSubmitReview = findViewById(R.id.btnSubmitReview);
        Button btnSubmitComplaint = findViewById(R.id.btnSubmitComplaint);
        CheckBox checkBoxAnon = findViewById(R.id.checkBoxAnonymous);
        EditText editTextRating = findViewById(R.id.editTextRating);
        EditText editTextReviewBody = findViewById(R.id.editTextReviewBody);
        EditText editTextComplaint = findViewById(R.id.editTextComplaint);

        // Set on click listener for back navigation button
        btnBackNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectedLessonActivity.this, BACK_NAV_DEST);
                startActivity(intent);
            }
        });

        // Set on-click listener for review submit button
        btnSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if inputs are filled
                if (editTextRating.getText().toString().isEmpty() || editTextReviewBody.getText().toString().isEmpty()) {
                    Toast.makeText(SelectedLessonActivity.this,
                            "Please fill all review fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check rating is in range
                Double rating = Double.parseDouble(editTextRating.getText().toString());
                if (rating < 1 || rating > 5) {
                    Toast.makeText(SelectedLessonActivity.this,
                            "Rating must be between 1 and 5.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if student is eligible for a review (no existent review for this tutor-topic pair
                // Define query condition
                ArrayList<DBHandler.QueryCondition> conditions = new ArrayList<>();
                conditions.add(new DBHandler.QueryCondition("studentId", "==", currentStudent.getId()));
                conditions.add(new DBHandler.QueryCondition("tutorId", "==", selectedLesson.getTutorId()));
                conditions.add(new DBHandler.QueryCondition("topicId", "==", selectedLesson.getTopicId()));

                // Perform query
                DBHandler.performQuery(REVIEW_COLLECTION, Review.class, conditions, new DBHandler.QueryCallback<Review>() {
                    @Override
                    public void onSuccess(ArrayList<Review> items) {
                        // If result isn't empty, toast and return
                        if (!items.isEmpty()) {
                            Toast.makeText(SelectedLessonActivity.this,
                                    "You're only allowed one review per tutor-topic pair!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Create review
                        Review review = new Review(
                                null,
                                selectedLesson.getStudentId(),
                                selectedLesson.getTutorId(),
                                selectedLesson.getTopicId(),
                                selectedLesson.getStudentName(),
                                selectedLesson.getTopicName(),
                                editTextReviewBody.getText().toString(),
                                new Date(),
                                rating,
                                checkBoxAnon.isChecked()
                        );

                        // Attempt to create review document
                        DBHandler.setDocument(null, REVIEW_COLLECTION, review, new DBHandler.SetDocumentCallback() {
                            @Override
                            public void onSuccess() {
                                // Toast success
                                Toast.makeText(SelectedLessonActivity.this, "Review published!", Toast.LENGTH_SHORT).show();

                                // Attempt to update tutor rating
                                FirebaseFirestore.getInstance().collection(DBHandler.TUTOR_COLLECTION)
                                        .document(selectedLesson.getTutorId())
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if(!documentSnapshot.exists()) return; //TODO: HANDLE FAILURE
                                                Tutor tutor = documentSnapshot.toObject(Tutor.class);
                                                assert tutor != null;
                                                tutor.updateRating(rating);
                                                DBHandler.setDocument(selectedLesson.getTutorId(), DBHandler.TUTOR_COLLECTION, tutor, new DBHandler.SetDocumentCallback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        // Nothing to do
                                                    }
                                                    @Override
                                                    public void onFailure(Exception e) {
                                                        // TODO: HANDLE FAILURE
                                                    }
                                                });
                                            }
                                        });
                                // TODO: HANDLE FAILURE
                            }
                            @Override
                            public void onFailure(Exception e) {
                                // Toast failure
                                Toast.makeText(SelectedLessonActivity.this,
                                        "Failed to publish review, try again later!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onFailure(Exception e) {
                        // Toast failure
                        Toast.makeText(SelectedLessonActivity.this,
                                "Sorry, we can't process your review right now!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Set on click listener for complaint submit button
        btnSubmitComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check input is filled
                if (editTextComplaint.getText().toString().isEmpty()) {
                    Toast.makeText(SelectedLessonActivity.this,
                            "Fill complaint description!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create complaint
                Complaint complaint = new Complaint(null, selectedLesson.getTutorId(), selectedLesson.getTutorName(), editTextComplaint.getText().toString());

                // Attempt to create complaint document in DB
                DBHandler.setDocument(null, COMPLAINT_COLLECTION, complaint, new DBHandler.SetDocumentCallback() {
                    @Override
                    public void onSuccess() {
                        // Toast success
                        Toast.makeText(SelectedLessonActivity.this,
                                "Complaint sent!", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(Exception e) {
                        // Toast failure
                        Toast.makeText(SelectedLessonActivity.this,
                                "Failed to send complaint, please try again later!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}