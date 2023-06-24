package com.example.tutron;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        EditText editTextNumDays = findViewById(R.id.editTextNumDays);
        Button btnBackNav = findViewById(R.id.btnComplaintBackNav);
        Button btnDismiss = findViewById(R.id.btnDismissComplaint);
        Button btnSuspendTutor = findViewById(R.id.btnSuspendTutor);

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

        // Set on click listener for dismiss button
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Attempt to delete complaint and navigate back
                DBHandler.deleteDocument(selectedComplaint.getId(), DBHandler.COMPLAINT_COLLECTION,
                        SelectedComplaintActivity.this, BACK_NAV_DEST);
            }
        });

        // Set on click listener for suspend tutor button
        btnSuspendTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If number of days input is empty, we suspend indefinitely (send numDays = null)
                String numDays = editTextNumDays.getText().toString();
                if (numDays.isEmpty()) numDays = null;
                // Attempt to suspend tutor, delete complaint, and navigate back
                DBHandler.suspendTutor(selectedComplaint, numDays,
                        SelectedComplaintActivity.this, BACK_NAV_DEST);
            }
        });
    }
}