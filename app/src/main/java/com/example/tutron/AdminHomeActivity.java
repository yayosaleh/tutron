package com.example.tutron;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class AdminHomeActivity extends AppCompatActivity {
    // List to store complaints from DB
    ArrayList<Complaint> complaintList;
    // Recycler view variables
    private RecyclerView complaintRecyclerView;
    private ComplaintAdapter complaintAdapter;
    private RecyclerView.LayoutManager complaintLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // Declare and initialize button variable
        Button btnLogOff = findViewById(R.id.btnAdminLogOff);

        // Set on click listener for log off button
        btnLogOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call log off method from util class
                AuthUtil.signOut(AdminHomeActivity.this);
            }
        });

        // Initialize RecyclerView
        populateList();
        populateRecyclerView();

    }

    @Override
    protected void onStart() {
        super.onStart();

        // If there is no current user, navigate back to main activity
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(AdminHomeActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }

    // GARBAGE
    private void populateList() {
        Complaint complaint = new Complaint(null, null, "Tutor 1", "Tutor showed up 40 minutes late" +
                "and charged me full price!");
        complaintList = new ArrayList<>();
        complaintList.add(complaint);
        complaintList.add(complaint);
        complaintList.add(complaint);
        complaintList.add(complaint);
        complaintList.add(complaint);
        complaintList.add(complaint);
        complaintList.add(complaint);
        complaintList.add(complaint);
        complaintList.add(complaint);
        complaintList.add(complaint);
        complaintList.add(complaint);
        complaintList.add(complaint);
        complaintList.add(complaint);
        complaintList.add(complaint);
        complaintList.add(complaint);
        complaintList.add(complaint);
        complaintList.add(complaint);
        complaintList.add(complaint);
        complaintList.add(complaint);
        complaintList.add(complaint);
    }

    public void populateRecyclerView() {
        // Get RV
        complaintRecyclerView = findViewById(R.id.recyclerViewComplaints);
        // Create layout manager -> pass activity
        complaintLayoutManager = new LinearLayoutManager(this);
        // Create adapter -> pass complaint list
        complaintAdapter = new ComplaintAdapter(complaintList);
        // Set layout
        complaintRecyclerView.setLayoutManager(complaintLayoutManager);
        // Set adapter
        complaintRecyclerView.setAdapter(complaintAdapter);
        // Set on item click listener
        complaintAdapter.setOnItemClickListener(new ComplaintAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // TEST
                complaintList.get(position).setTutorName("CLICKED");
                complaintAdapter.notifyItemChanged(position);
            }
        });
    }
}