package com.example.tutron;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize component vars
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegisterStudent = findViewById(R.id.btnRegisterStudent);
        Button btnRegisterTutor = findViewById(R.id.btnRegisterTutor);

        // Set on click listener for login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Attempt to sign user in and navigate to welcome page
                AuthUtil.signIn(editTextEmail.getText().toString(),
                        editTextPassword.getText().toString(), MainActivity.this);
            }
        });

        // Set on click listener for student registration button (navigation)
        btnRegisterStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StudentRegistrationActivity.class);
                startActivity(intent);
            }
        });

        // Set on click listener for student registration button (navigation)
        btnRegisterTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TutorRegistrationActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if there is a user currently logged in (sign out if so)
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) mAuth.signOut();
        // TODO: consider alternative design where we navigate user to welcome page in this case
    }
}