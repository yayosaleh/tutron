package com.example.tutron;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class LessonRequestActivity extends AppCompatActivity {
    private static final Class<?> BACK_NAV_DEST = StudentHomeActivity.class;
    private static final String TIMESLOT_COLLECTION = "timeslots";
    private Student currentStudent;
    private Tutor selectedTutor;
    private Topic selectedTopic;
    private ArrayList<Timeslot> timeslotList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_request);

        // Get current student
        currentStudent = DataManager.getInstance().getCurrentStudent();

        // Get tutor and topic passed via intent
        selectedTutor = getIntent().getParcelableExtra("Selected Tutor");
        selectedTopic = getIntent().getParcelableExtra("Selected Topic");

        // Update instruction message with selected topic
        TextView textViewInstruction = findViewById(R.id.textViewLessonRequestInstruction);
        String instructionMessage = "Tap on a timeslot to request a " + selectedTopic.getName() + " lesson at that time.";
        textViewInstruction.setText(instructionMessage);

        // Set on click listener for back navigation button
        Button btnBackNav = findViewById(R.id.btnLessonRequestBackNav);
        btnBackNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LessonRequestActivity.this, BACK_NAV_DEST);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Populate timeslot list
        populateTimeslotList();
    }

    private void populateTimeslotList() {
        // Define query conditions
        ArrayList<DBHandler.QueryCondition> conditions = new ArrayList<>();
        conditions.add(new DBHandler.QueryCondition("tutorId", "==", selectedTutor.getId()));
        conditions.add(new DBHandler.QueryCondition("booked", "==", false));

        // Perform query
        DBHandler.performQuery(TIMESLOT_COLLECTION, Timeslot.class, conditions, new DBHandler.QueryCallback<Timeslot>() {
            @Override
            public void onSuccess(ArrayList<Timeslot> items) {
                // Build recycler view
                timeslotList = items;
                buildRecyclerView();
            }

            @Override
            public void onFailure(Exception e) {
                // Toast failure
                Toast.makeText(LessonRequestActivity.this, "Failed to get this tutor's availability, try again later.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildRecyclerView () {
        // Define RV bindings for adapter
        GenericRVAdapter.Binder<Timeslot> binder = new GenericRVAdapter.Binder<Timeslot>() {
            @Override
            public void bindData(Timeslot timeslot, GenericRVAdapter.GenericViewHolder holder) {
                // Get text view
                TextView textViewDescription = (TextView) holder.getViewById(R.id.textViewTimeslotItemDescription);
                // Bind text view
                textViewDescription.setText(timeslot.toString());
            }
        };
        // Get RecyclerView
        RecyclerView recyclerViewTimeslots = findViewById(R.id.recyclerViewTimeslots);
        // Create layout manager
        RecyclerView.LayoutManager timeslotLayoutManager = new LinearLayoutManager(this);
        // Create adapter
        GenericRVAdapter<Timeslot> timeslotAdapter = new GenericRVAdapter<>(timeslotList, R.layout.timeslot_item, binder);
        // Set layout manager
        recyclerViewTimeslots.setLayoutManager(timeslotLayoutManager);
        // Set adapter
        recyclerViewTimeslots.setAdapter(timeslotAdapter);
        // TODO: on item click listener that creates lesson
    }
}