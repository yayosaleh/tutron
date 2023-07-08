package com.example.tutron;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TutorProfileActivity extends AppCompatActivity {
    private static final Class<?> MANAGE_TOPICS_DEST = TopicManagerActivity.class;
    private Tutor currentTutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_profile);

        // Get current tutor passed via intent
        Intent intent = getIntent();
        currentTutor = intent.getParcelableExtra("Current Tutor");

        // Declare and initialize view variables
        TextView textViewWelcomeMessage = findViewById(R.id.textViewTutorProfileWelcomeMessage);
        Button btnLogOff = findViewById(R.id.btnTutorProfileLogOff);
        Button btnManageTopics = findViewById(R.id.btnManageTopics);

        // Set welcome message text view
        String welcomeMessage = "Welcome, " + currentTutor.getFirstName() +
                " " + currentTutor.getLastName() + "!";
        textViewWelcomeMessage.setText(welcomeMessage);

        // Set on click listeners for log off and manage topics buttons
        btnLogOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call log off method from util class
                AuthUtil.signOut(TutorProfileActivity.this);
            }
        });

        btnManageTopics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TutorProfileActivity.this, MANAGE_TOPICS_DEST);
                intent.putExtra("Current Tutor", currentTutor);
                startActivity(intent);
            }
        });
    }
}