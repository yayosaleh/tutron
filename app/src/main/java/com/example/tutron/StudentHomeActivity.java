package com.example.tutron;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;

public class StudentHomeActivity extends AppCompatActivity {

    private static final String TAG = "StudentHomeActivity";
    private static final Class<?> TUTOR_SEARCH_DEST = TutorSearchActivity.class;
    private static final Class<?> MANAGE_LESSONS_DEST = StudentLessonManagerActivity.class;
    private static final String STUDENT_COLLECTION = "students";
    private Student currentStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        // Declare and initialize view variables
        Button btnLogOff = findViewById(R.id.btnStudentLogOff);
        Button btnManageLessons = findViewById(R.id.btnStudentManageLessons);
        Button btnSearch = findViewById(R.id.btnTutorSearch);
        EditText editTextSearchFirstName = findViewById(R.id.editTextSearchFirstName);
        EditText editTextSearchLastName = findViewById(R.id.editTextSearchLastName);
        EditText editTextSearchLanguage = findViewById(R.id.editTextSearchLanguage);
        EditText editTextSearchTopic = findViewById(R.id.editTextSearchTopicName);

        // Create list of edit texts for easy validation
        ArrayList<EditText> editTexts = new ArrayList<>(Arrays.asList(
                editTextSearchFirstName,
                editTextSearchLastName,
                editTextSearchLanguage,
                editTextSearchTopic
        ));

        // Set on click listener for log off button
        btnLogOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call log off method from util class
                AuthUtil.signOut(StudentHomeActivity.this);
            }
        });

        // Set on click listener for manage lessons button
        btnManageLessons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentHomeActivity.this, MANAGE_LESSONS_DEST);
                startActivity(intent);
            }
        });

        // Set on click listener for search button
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ensure current tutor is not null
                if (isCurrentStudentNull()) return;

                // Check at least one field is filled
                int numFilledFields = 0;
                for (EditText editText : editTexts) {
                    if (!editText.getText().toString().isEmpty()) numFilledFields++;
                }
                if (numFilledFields < 1) {
                    Toast.makeText(StudentHomeActivity.this, "Please fill at least one search field!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Extract text from fields
                String firstName = editTextSearchFirstName.getText().toString();
                String lastName = editTextSearchLastName.getText().toString();
                String language = TopicManagerActivity.capitalizeFirstLetterOfEachWord(editTextSearchLanguage.getText().toString());
                String topicName = editTextSearchTopic.getText().toString().toLowerCase();

                // Generate query conditions (order is important)
                ArrayList<DBHandler.QueryCondition> conditions = new ArrayList<>();
                conditions.add(new DBHandler.QueryCondition("suspensionExpiry", "==", null));
                if (!firstName.isEmpty()) {
                    conditions.add(new DBHandler.QueryCondition("firstName", "==", firstName));
                }
                if (!lastName.isEmpty()) {
                    conditions.add(new DBHandler.QueryCondition("lastName", "==", lastName));
                }
                if (!language.isEmpty()) {
                    conditions.add(new DBHandler.QueryCondition("nativeLanguage", "==", language));
                }
                if (!topicName.isEmpty()) {
                    conditions.add(new DBHandler.QueryCondition("offeredTopicNames", "array-contains", topicName));
                }

                // Perform query
                DBHandler.performQuery(DBHandler.TUTOR_COLLECTION, Tutor.class, conditions, new DBHandler.QueryCallback<Tutor>() {
                    @Override
                    public void onSuccess(ArrayList<Tutor> items) {
                        // Toast and return if there are no matches
                        if (items.isEmpty()) {
                            Toast.makeText(StudentHomeActivity.this, "No matches!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Send tutor list to TutorSearchActivity and navigate
                        Intent intent = new Intent(StudentHomeActivity.this, TUTOR_SEARCH_DEST);
                        intent.putParcelableArrayListExtra("Tutors", items);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Toast failure
                        Toast.makeText(StudentHomeActivity.this, "Failed to perform search, please try again!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // If there is no current user, navigate back to main activity
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(StudentHomeActivity.this, MainActivity.class);
            startActivity(intent);
            return;
        }

        // Get student data from DB
        // Return to main activity in case of failure

        // Access shared Firestore database instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get tutor document associated with current user -> store in currentTutor
        db.collection(STUDENT_COLLECTION).document(user.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) return; // TODO: handle
                        currentStudent = documentSnapshot.toObject(Student.class);
                        /*
                        Set current student for downstream activities
                        We can do this asynchronously because we only every call set...() here
                        since current student's state never changes!
                         */
                        DataManager.getInstance().setCurrentStudent(currentStudent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log and toast failure
                        Log.w(TAG, "getStudent*:failure", e);
                        String message = "Failed to get student data! " + e.getMessage();
                        Toast.makeText(StudentHomeActivity.this, message, Toast.LENGTH_SHORT).show();
                        // Navigate back to main activity
                        Intent intent = new Intent(StudentHomeActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
    }

    private Boolean isCurrentStudentNull() {
        if (currentStudent == null) {
            // Ask user to wait while we asynch fetch is completed
            Toast.makeText(StudentHomeActivity.this, "Please wait while we get your data.",
                    Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}