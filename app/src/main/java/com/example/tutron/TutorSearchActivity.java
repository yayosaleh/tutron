package com.example.tutron;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class TutorSearchActivity extends AppCompatActivity {
    private static final Class<?> BACK_NAV_DEST = StudentHomeActivity.class;
    private static final Class<?> ON_CLICK_DEST = TutorLandingActivity.class;
    private Student currentStudent;
    private ArrayList<Tutor> tutorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_search);

        // Get current student (only need to do this here since student state never changes)
        currentStudent = DataManager.getInstance().getCurrentStudent();

        // Set on click listener for back navigation button
        Button btnBackNav = findViewById(R.id.btnTutorSearchBackNav);
        btnBackNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TutorSearchActivity.this, BACK_NAV_DEST);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Get list of tutors passed via intent
        tutorList = getIntent().getParcelableArrayListExtra("Tutors");

        // Build recycler view
        buildRecyclerView();
    }

    // Initializes recycler view
    private void buildRecyclerView() {

        // Define bindings for adapter
        GenericRVAdapter.Binder<Tutor> binder = new GenericRVAdapter.Binder<Tutor>() {
            @Override
            public void bindData(Tutor tutor, GenericRVAdapter.GenericViewHolder holder) {
                // Get the whole tutor_item view from the holder
                View tutorItemView = holder.itemView;
                // Create copy of tutor to hide description
                Tutor tutorCopy = tutor.copy();
                tutorCopy.setDescription("");
                // Bind tutor to the view
                tutorCopy.bindToView(tutorItemView);
            }
        };

        // Get RV
        RecyclerView recyclerViewTutors = findViewById(R.id.recyclerViewTutors);
        // Create layout manager
        RecyclerView.LayoutManager tutorLayoutManager = new LinearLayoutManager(this);
        // Create adapter
        GenericRVAdapter<Tutor> tutorAdapter = new GenericRVAdapter<>(tutorList, R.layout.tutor_item, binder);
        // Set layout manager
        recyclerViewTutors.setLayoutManager(tutorLayoutManager);
        // Set adapter
        recyclerViewTutors.setAdapter(tutorAdapter);
        // TODO: item click listener
        tutorAdapter.setOnItemClickListener(new GenericRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Navigate to tutor landing and send selected tutor
                Intent intent = new Intent(TutorSearchActivity.this, ON_CLICK_DEST);
                intent.putExtra("Selected Tutor", tutorList.get(position));
                startActivity(intent);
            }
        });
    }
}