package com.example.tutron;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SelectedComplaintActivity extends AppCompatActivity {
    private static final Class<?> BACK_NAV_DEST = AdminHomeActivity.class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_complaint);

        // Get selected complaint passed via intent
        Intent intent = getIntent();
        Complaint selectedComplaint = intent.getParcelableExtra("Selected Complaint");

        // Declare and initialize views
        TextView textViewTutorName = findViewById(R.id.textViewSelectedTutorName);
        TextView textViewComplaintDescription = findViewById(R.id.textViewSelectedComplaintDescription);
        Button btnBackNav = findViewById(R.id.btnComplaintBackNav);

        // Set text views
        textViewTutorName.setText(selectedComplaint.getTutorName());
        textViewComplaintDescription.setText(selectedComplaint.getDescription());

        // Set on click listener for back navigation button
        btnBackNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectedComplaintActivity.this, BACK_NAV_DEST);
                startActivity(intent);
            }
        });
    }
}