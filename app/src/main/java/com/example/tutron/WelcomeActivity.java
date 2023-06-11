package com.example.tutron;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class WelcomeActivity extends AppCompatActivity {

    private static final String STUDENT_COLLECTION = "students";
    private static final String TUTOR_COLLECTION = "tutors";
    private static final String ADMIN_COLLECTION = "administrators";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Identify role of current user and update text view
        identifyRole();
    }

    // Identifies role of current user and updates text view
    private void identifyRole(){
        // Get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // TODO: handle case where there's no user logged in (which shouldn't be possible)
            return;
        }

        // Access shared FirebaseFirestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Search each collection
        // ***Currently inefficient due to lack of stopping condition
        String[] collections = {STUDENT_COLLECTION, TUTOR_COLLECTION, ADMIN_COLLECTION};
        for (String collection : collections) {
            db.collection(collection).document(user.getUid()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            // Update text view using current collection
                            String message = "You are logged in as a " +
                                    collection.substring(0, collection.length() - 1) + "!";
                            TextView roleIdentifierText = findViewById(R.id.roleIdentifierText);
                            roleIdentifierText.setText(message);
                        }
                    });
            // TODO: add on failure listener?
        }
    }
}