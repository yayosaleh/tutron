package com.example.tutron;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class TopicManagerActivity extends AppCompatActivity {

    private static final Class<?>  BACK_NAV_DEST = TutorProfileActivity.class;
    private static final Class<?> ADD_TOPIC_DEST = TopicAdderActivity.class;
    private static final int MAX_NUM_OFFERED_TOPICS = 5;

    private Tutor currentTutor;
    // TODO DON'T CONSTRUCT ARRAYS HERE!
    private ArrayList<Topic> topics = new ArrayList<>();
    private ArrayList<Topic> offeredTopics = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_manager);

        // Get current tutor passed via intent
        Intent intent = getIntent();
        currentTutor = intent.getParcelableExtra("Current Tutor");

        // Initialize view variables
        Button btnBackNav = findViewById(R.id.btnTopicManagerBackNav);
        Button btnAddTopicNav = findViewById(R.id.btnAddTopicNav);

        // Set on click listeners for back and add topic navigation
        btnBackNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Must send current tutor back to repopulate intent
                Intent intent = new Intent(TopicManagerActivity.this, BACK_NAV_DEST);
                intent.putExtra("Current Tutor", currentTutor);
                startActivity(intent);
            }
        });

        btnAddTopicNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Must send current tutor to repopulate intent upon back navigation (and to access id)
                // Must send current number of topics to limit number of topics added
                Intent intent = new Intent(TopicManagerActivity.this, ADD_TOPIC_DEST);
                intent.putExtra("Current Tutor", currentTutor);
                intent.putExtra("Number of Topics", topics.size());
                startActivity(intent);
            }
        });
    }
}