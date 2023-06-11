package com.example.tutron;

import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RegistrationUtil {

    private static final String TAG = "RegistrationUtil";
    private static final String STUDENT_COLLECTION = "students";

    // Returns true if all text inputs are filled
    public static boolean validTextInputs(ArrayList<EditText> editTexts){
        for (int i = 0; i < editTexts.size(); i++){
            EditText elem = editTexts.get(i);
            if (elem.getText().toString().trim().isEmpty()) return false;
        }
        return true;
    }

    // Attempts to create account with given credentials and sign user in (Firebase)
    // Calls createStudent() if successful
    public static void createAccount(String email, String password, Student student, Tutor tutor, android.content.Context context){

        // Access shared FirebaseAuth instance
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Account creation is async., operations must be done inside OnCompleteListener
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Account successfully created and user signed in
                            // Current user can be accessed app-wide using shared FirebaseAuth instance

                            // Log success
                            Log.d(TAG, "createUser:success");

                            // Call student creation method
                            RegistrationUtil.createStudent(student, context);

                        } else {
                            // Account creation failed

                            // Log failure
                            Log.w(TAG, "createUser:failure", task.getException());

                            // Create toast with cause of failure
                            String failureMessage = (task.getException() != null)?
                                    task.getException().getMessage(): "Could not create account with these credentials.";
                            Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Attempts to create Student document (Firestore)
    // Navigates to WelcomeActivity if successful
    public static void createStudent(Student student, android.content.Context context){

        // Access shared FirebaseAuth and FirebaseFirestore instances
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Ensure current user is logged in
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            Toast.makeText(context, "Error occurred. Try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase user ID and Firestore document ID must be the same
        String userID = user.getUid();

        // Creating collections and documents in Firestore is implicit!
        db.collection(STUDENT_COLLECTION).document(userID).set(student)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Create toast to mark success
                        Toast.makeText(context, "Student registered!", Toast.LENGTH_SHORT).show();

                        // Navigate to welcome page
                        Intent intent = new Intent(context, WelcomeActivity.class);
                        context.startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log and toast failure
                        Log.w(TAG, "createStudent:failure", e);
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
