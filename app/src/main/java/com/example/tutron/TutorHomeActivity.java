package com.example.tutron;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TutorHomeActivity extends AppCompatActivity {

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
        }

        //TODO: check suspension status and change text view accordingly (add return to above statement)

    }
}