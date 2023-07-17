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

import java.util.ArrayList;

public class TutorLandingActivity extends AppCompatActivity {
    private static final Class<?> BACK_NAV_DEST = StudentHomeActivity.class;
    private static final Class<?> ON_CLICK_DEST = LessonRequestActivity.class;
    private static final String TOPIC_COLLECTION = "topics";
    private Student currentStudent;
    private Tutor selectedTutor;
    private ArrayList<Topic> topicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_landing);

        // Get current student
        currentStudent = DataManager.getInstance().getCurrentStudent();

        // Get selected tutor passed via intent
        selectedTutor = getIntent().getParcelableExtra("Selected Tutor");

        // Bind selected tutor to reusable tutor item view
        View tutorItemView = findViewById(R.id.tutorItem);
        selectedTutor.bindToView(tutorItemView);

        // Set on click listener for back navigation button
        Button btnBackNav = findViewById(R.id.btnTutorLandingBackNav);
        btnBackNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TutorLandingActivity.this, BACK_NAV_DEST);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Populate topic list
        populateTopicList();
    }

    // Populates list of topics offered by selected tutor and calls buildTopicsRecyclerView()
    private void populateTopicList() {
        // Define query conditions
        ArrayList<DBHandler.QueryCondition> conditions = new ArrayList<>();
        conditions.add(new DBHandler.QueryCondition("tutorId", "==", selectedTutor.getId()));
        conditions.add(new DBHandler.QueryCondition("offered", "==", true));

        // Perform query
        DBHandler.performQuery(TOPIC_COLLECTION, Topic.class, conditions, new DBHandler.QueryCallback<Topic>() {
            @Override
            public void onSuccess(ArrayList<Topic> items) {
                // Toast and return if no topics found
                if (items.isEmpty()) {
                    Toast.makeText(TutorLandingActivity.this, "This tutor has no offered topics.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // Build topics recycler view
                topicList = items;
                buildTopicsRecyclerView();
            }
            @Override
            public void onFailure(Exception e) {
                // Toast failure
                Toast.makeText(TutorLandingActivity.this, "Failed to get this tutor's topics, try again later.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildTopicsRecyclerView() {
        // Define RV bindings for adapter
        GenericRVAdapter.Binder<Topic> binder = new GenericRVAdapter.Binder<Topic>() {
            @Override
            public void bindData(Topic topic, GenericRVAdapter.GenericViewHolder holder) {
                // Get text views
                TextView textViewOfferingStatus = (TextView) holder.getViewById(R.id.textViewTopicOfferingStatus);
                TextView textViewName = (TextView) holder.getViewById(R.id.textViewTopicName);
                TextView textViewYearsOfExperience = (TextView) holder.getViewById(R.id.textViewTopicYearsOfExperience);
                TextView textViewDescription = (TextView) holder.getViewById(R.id.textViewTopicDescription);

                // Bind text views
                textViewOfferingStatus.setVisibility(View.GONE); // Hide offering status
                textViewName.setText(TopicManagerActivity.capitalizeFirstLetterOfEachWord(topic.getName()));
                String experienceMessage = topic.getYearsOfExperience() + " yr(s)";
                textViewYearsOfExperience.setText(experienceMessage);
                textViewDescription.setText(topic.getDescription());
            }
        };
        // Get RV
        RecyclerView recyclerViewOfferedTopics = findViewById(R.id.recyclerViewOfferedTopics);
        // Create layout manager
        RecyclerView.LayoutManager topicLayoutManager = new LinearLayoutManager(this);
        //Create adapter
        GenericRVAdapter<Topic> topicAdapter = new GenericRVAdapter<>(topicList, R.layout.topic_item, binder);
        // Set layout manager
        recyclerViewOfferedTopics.setLayoutManager(topicLayoutManager);
        // Set adapter
        recyclerViewOfferedTopics.setAdapter(topicAdapter);
        // Set on item click listener (navigate to lesson request activity)
        topicAdapter.setOnItemClickListener(new GenericRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Navigate to lesson request activity and pass selected tutor and topic
                Intent intent = new Intent(TutorLandingActivity.this, ON_CLICK_DEST);
                intent.putExtra("Selected Tutor", selectedTutor);
                intent.putExtra("Selected Topic", topicList.get(position));
                startActivity(intent);
            }
        });
    }
}