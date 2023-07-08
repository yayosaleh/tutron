package com.example.tutron;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class TutorProfileActivity extends AppCompatActivity {
    private Tutor currentTutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_profile);

        // Get current tutor passed via intent
        Intent intent = getIntent();
        currentTutor = intent.getParcelableExtra("Current Tutor");
    }
}