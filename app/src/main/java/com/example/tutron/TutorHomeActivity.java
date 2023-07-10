package com.example.tutron;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TutorHomeActivity extends AppCompatActivity {
    private static final String TAG = "TutorHomeActivity";
    private static final Class<?> UNSUSPENDED_DEST = TutorProfileActivity.class;
    private Tutor currentTutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_home);

        // Declare and initialize button variable
        Button btnLogOff = findViewById(R.id.btnTutorLogOff);

        // Set on click listener for log off button
        btnLogOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call log off method from util class
                AuthUtil.signOut(TutorHomeActivity.this);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        // If there is no current user, navigate back to main activity
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(TutorHomeActivity.this, MainActivity.class);
            startActivity(intent);
            return;
        }

        // Get tutor data from DB and check suspension status
        // Return to main activity in case of failure

        // Access shared Firestore database instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get tutor document associated with current user -> store in currentTutor
        db.collection(DBHandler.TUTOR_COLLECTION).document(user.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) return; // TODO: handle
                        currentTutor = documentSnapshot.toObject(Tutor.class);
                        checkSuspensionStatus();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log and toast failure
                        Log.w(TAG, "getTutor*:failure", e);
                        String message = "Failed to get tutor data! " + e.getMessage();
                        Toast.makeText(TutorHomeActivity.this, message, Toast.LENGTH_SHORT).show();
                        // Navigate back to main activity
                        Intent intent = new Intent(TutorHomeActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
    }

    // Checks suspension status and navigates or changes text view accordingly
    private void checkSuspensionStatus() {
        String suspensionExpiry = currentTutor.getSuspensionExpiry();

        // Navigate to TutorProfileActivity if tutor is not suspended (and return)
        if (suspensionExpiry == null) {
            Intent intent = new Intent(TutorHomeActivity.this, UNSUSPENDED_DEST);
            intent.putExtra("Current Tutor", currentTutor);
            startActivity(intent);
            return;
        }

        // Return if tutor is no longer suspended
        if (!suspensionExpiry.equals(Tutor.INDEF_SUSPENSION) &&
                DateTimeHandler.isInPastOrPresent(
                        DateTimeHandler.stringToDate(suspensionExpiry))) return;

        // Formulate suspension message
        String suspensionMessage = "You are suspended ";
        if (suspensionExpiry.equals(Tutor.INDEF_SUSPENSION)) {
            suspensionMessage += "indefinitely! \n You can no longer use Tutron \uD83D\uDCA9";
        } else {
            suspensionMessage += "temporarily! \n Your suspension will be lifted on "
                    + suspensionExpiry + " \u23F0";
        }

        // Set welcome message text view to suspension message
        TextView textViewWelcomeMessage = findViewById(R.id.textViewTutorWelcomeMessage);
        textViewWelcomeMessage.setText(suspensionMessage);

        // TODO: Prevent further user interaction if suspended
        //  Hide buttons if tutor is suspended (via some flag), OR
        //  Set a timer and send user back to main activity, OR
        //  Make this a proxy activity
    }
}