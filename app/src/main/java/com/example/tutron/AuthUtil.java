package com.example.tutron;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AuthUtil {

    private static final String TAG = "AuthUtil";
    private static final String STUDENT_COLLECTION = "students";
    private static final String TUTOR_COLLECTION = "tutors";
    private static final String ADMIN_COLLECTION = "administrators";
    private static final Map<String, Class<?>> roleToHomeActivityMap = new HashMap<String, Class<?>>() {{
        put(STUDENT_COLLECTION, StudentHomeActivity.class);
        put(TUTOR_COLLECTION, TutorHomeActivity.class);
        put(ADMIN_COLLECTION, AdminHomeActivity.class);
    }};

    // Access shared FirebaseAuth instance
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // Attempts to sign user in (Firebase)
    // Calls identifyRole() if successful
    public static void signIn(String email, String password, Context context){
        // Attempt to sign in user with email and password (Firebase)
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in successful

                            // Log and toast success
                            Log.d(TAG, "signIn:success");
                            Toast.makeText(context, "Signed in!", Toast.LENGTH_SHORT).show();

                            // Identify role and navigate to appropriate home activity
                            identifyRole(context);
                        } else {
                            // Sign in failed

                            // Log and toast cause of failure
                            Log.w(TAG, "signIn:failure", task.getException());
                            String failureMessage = (task.getException() != null)?
                                    task.getException().getMessage(): "Authentication failed.";
                            Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Signs user out and navigates to main activity
    public static void signOut(Context context){
        // Sign user out
        mAuth.signOut();

        // Log and toast success
        Log.d(TAG, "signOut:success");
        Toast.makeText(context, "Signed out!", Toast.LENGTH_SHORT).show();

        // Navigate to main activity
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    // Identifies role of current user and calls navigateToHomeActivity()
    private static void identifyRole(Context context){
        // Get current user (guaranteed to be non-null by signIn())
        FirebaseUser user = mAuth.getCurrentUser();

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
                            // Log result
                            Log.d(TAG, "User found in '" + collection + "'.");
                            // Call navigateToHomeActivity() using current collection
                            navigateToHomeActivity(collection, context);
                        }
                    });
            // TODO: add on failure listener?
        }
    }

    // Navigates current user to appropriate home activity
    private static void navigateToHomeActivity(String role, Context context){
        Class<?> activity = roleToHomeActivityMap.get(role);
        Intent intent = new Intent(context, activity);
        context.startActivity(intent);
    }
}
