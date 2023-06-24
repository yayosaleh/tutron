package com.example.tutron;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminHomeActivity extends AppCompatActivity {
    private static final String TAG = "AdminHomeActivity";
    private static final Class<?> ITEM_ON_CLICK_DEST = SelectedComplaintActivity.class;

    // List to store complaints from DB
    ArrayList<Complaint> complaintList;

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

        // Populate list of complaints from DB everytime activity is navigated to
        populateList();
    }

    // Gets complaints from DB and populates complaints list
    // Builds RecyclerView if successful
    private void populateList() {
        // Access shared Firestore database instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Attempt to get complaint collection
        db.collection(DBHandler.COMPLAINT_COLLECTION).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        // Populate complaint list
                        complaintList = DBHandler.querySnapshotToList(querySnapshot, Complaint.class);
                        // Build RecyclerView
                        buildRecyclerView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log and toast failure
                        Log.w(TAG, "Failed to load complaints.", e);
                        String message = "Failed to load complaints. " + e.getMessage();
                        Toast.makeText(AdminHomeActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Builds RecyclerView if complaints list is non-emtpy
    public void buildRecyclerView() {
        // Check if complaint list is empty, if so -> toast -> return
        if (complaintList.isEmpty()) {
            Toast.makeText(AdminHomeActivity.this, "There are no complaints!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Get RV
        RecyclerView complaintRecyclerView = findViewById(R.id.recyclerViewComplaints);
        // Create layout manager -> pass activity
        RecyclerView.LayoutManager complaintLayoutManager = new LinearLayoutManager(this);
        // Create adapter -> pass complaint list
        ComplaintAdapter complaintAdapter = new ComplaintAdapter(complaintList);
        // Set layout
        complaintRecyclerView.setLayoutManager(complaintLayoutManager);
        // Set adapter
        complaintRecyclerView.setAdapter(complaintAdapter);
        // Set on item click listener
        complaintAdapter.setOnItemClickListener(new ComplaintAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Navigate to SelectedComplaintActivity and send selected Complaint
                Intent intent = new Intent(AdminHomeActivity.this, ITEM_ON_CLICK_DEST);
                intent.putExtra("Selected Complaint", complaintList.get(position));
                startActivity(intent);
            }
        });
    }
}