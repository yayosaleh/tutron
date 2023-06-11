package com.example.tutron;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";
    private static final String STUDENT_COLLECTION = "students";
    private static final String TUTOR_COLLECTION = "tutors";
    private static final String ADMIN_COLLECTION = "administrators";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Declare and initialize button variable
        Button btnLogOff = findViewById(R.id.btnLogOff);

        // Set on click listener for log off button
        btnLogOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call log off method from util class
                AuthUtil.signOut(WelcomeActivity.this);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        // If there is no current user, navigate back to main activity
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            return;
        }

        // Identify role of current user and update text view
        identifyRole();
    }

    // Identifies role of current user and updates text view
    private void identifyRole(){
        // Get current user (guaranteed to be logged in by onStart())
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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
                            // Method entered when query is successful, not if doc was found
                            // Check if document exists
                            if (!documentSnapshot.exists()) return;
                            // Update text view using current collection
                            String role = collection.substring(0, collection.length() - 1);
                            String message = "You are logged in as a " + role + "!";
                            TextView roleIdentifierText = findViewById(R.id.roleIdentifierText);
                            roleIdentifierText.setText(message);
                            // Log result
                            Log.d(TAG, role + " found.");
                        }
                    });
            // TODO: add on failure listener?
        }
    }
}