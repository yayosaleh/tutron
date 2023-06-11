package com.example.tutron;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthUtil {

    // TODO: replace hardcoded navigation destinations with constants

    private static final String TAG = "AuthUtil";

    // Attempts to sign user in (Firebase)
    // Navigates to welcome activity if successful
    public static void signIn(String email, String password, android.content.Context context){
        // Access shared Firebase instance
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

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

                            // Navigate to welcome page
                            Intent intent = new Intent(context, WelcomeActivity.class);
                            context.startActivity(intent);
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
    public static void signOut(android.content.Context context){
        // Access shared Firebase instance and sign user out
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        // Create toast message
        Toast.makeText(context, "Signed out!", Toast.LENGTH_SHORT).show();

        //TODO: log result

        // Navigate to main activity
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

}
