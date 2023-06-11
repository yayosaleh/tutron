package com.example.tutron;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;

public class StudentRegistrationActivity extends AppCompatActivity {

    public static final String TAG = "StudentRegistrationActivity";
    public static final String STUDENT_COLLECTION = "students";

    private EditText editTextFirstName, editTextLastName, editTextEmail,
            editTextPassword, editTextAddress, editTextCity,
            editTextProvince, editTextPostalCode, editTextCreditCard,
            editTextExpiry, editTextCVV;

    private Button btnSubmit;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);

        // Initialize component variables
        editTextFirstName = findViewById(R.id.editTextStudentFirstName);
        editTextLastName = findViewById(R.id.editTextStudentLastName);
        editTextEmail = findViewById(R.id.editTextStudentEmail);
        editTextPassword = findViewById(R.id.editTextStudentPassword);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextCity = findViewById(R.id.editTextCity);
        editTextProvince = findViewById(R.id.editTextProvince);
        editTextPostalCode = findViewById(R.id.editTextPostalCode);
        editTextCreditCard = findViewById(R.id.editTextCreditCard);
        editTextExpiry = findViewById(R.id.editTextExpiry);
        editTextCVV = findViewById(R.id.editTextCVV);
        btnSubmit = findViewById(R.id.btnStudentSubmit);

        // Create ArrayList with all editTexts for easy validation
        ArrayList<EditText> editTexts = new ArrayList<>(Arrays.asList(
                editTextFirstName,
                editTextLastName,
                editTextEmail,
                editTextPassword,
                editTextAddress,
                editTextCity,
                editTextProvince,
                editTextPostalCode,
                editTextCreditCard,
                editTextExpiry,
                editTextCVV
        ));

        // Access shared FirebaseAuth and FirebaseFireStore instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Create on click listener for submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check all text inputs are non-empty
                if (!validTextInputs(editTexts)){
                    Toast.makeText(StudentRegistrationActivity.this,
                            "Please fill all fields!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create Student object
                String address = editTextAddress.getText().toString() + "," +
                        editTextCity.getText().toString() + "," +
                        editTextProvince.getText().toString() + "," +
                        editTextPostalCode.getText().toString();

                String creditCardInfo = editTextCreditCard.getText().toString() + "," +
                        editTextExpiry.getText().toString() + "," +
                        editTextCVV.getText().toString();

                Student student = new Student(editTextFirstName.getText().toString(),
                        editTextLastName.getText().toString(), address, creditCardInfo);

                // Attempt to create account and register student
                createAccount(editTextEmail.getText().toString(),
                        editTextPassword.getText().toString(), student);
            }
        });
    }

    // Returns true if all text inputs are filled
    private boolean validTextInputs(ArrayList<EditText> editTexts){
        for (int i = 0; i < editTexts.size(); i++){
            EditText elem = editTexts.get(i);
            if (elem.getText().toString().trim().isEmpty()) return false;
        }
        return true;
    }

    // Attempts to create account with given credentials and sign user in (Firebase)
    // Calls createStudent() if successful
    private void createAccount(String email, String password, Student student){
        // Account creation is async., operations must be done inside OnCompleteListener
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Account successfully created and user signed in
                            // Current user can be accessed app-wide using shared FirebaseAuth instance

                            // Log success
                            Log.d(TAG, "createUserWithEmail:success");

                            // Call student creation method
                            createStudent(student);

                        } else {
                            // Account creation failed

                            // Log failure
                            Log.w(TAG, "createUser:failure", task.getException());

                            // Create toast with cause of failure
                            String failureMessage = (task.getException() != null)?
                                    task.getException().getMessage(): "Could not create account with these credentials.";
                            Toast.makeText(StudentRegistrationActivity.this, failureMessage,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Attempts to create Student document (Firestore)
    // Navigates to WelcomeActivity if successful
    private void createStudent(Student student){
        // Ensure current user is logged in
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            Toast.makeText(StudentRegistrationActivity.this, "Error occurred. Try again.",
                    Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(StudentRegistrationActivity.this, "Student registered!",
                                Toast.LENGTH_SHORT).show();

                        // Navigate to welcome page
                        Intent intent = new Intent(StudentRegistrationActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log and toast failure
                        Log.w(TAG, "createStudent:failure", e);
                        Toast.makeText(StudentRegistrationActivity.this, e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}