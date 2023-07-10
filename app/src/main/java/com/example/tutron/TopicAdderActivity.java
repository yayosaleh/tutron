package com.example.tutron;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class TopicAdderActivity extends AppCompatActivity {

    private static final Class<?> BACK_NAV_DEST = TopicManagerActivity.class;
    private static final int MAX_NUM_TOPICS = 20;
    private static final String TOPIC_COLLECTION = "topics";

    private int numTopics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_adder);

        // Get tutor ID and current number of topics passed via intent

        Intent intent = getIntent();
        Tutor currentTutor = intent.getParcelableExtra("Current Tutor");
        numTopics = intent.getIntExtra("Number of Topics", 20); // Set default to max as safeguard

        // Initialize view variables

        EditText editTextTopicName = findViewById(R.id.editTextTopicName);
        EditText editTextYearsOfExperience = findViewById(R.id.editTextTopicYearsOfExperience);
        EditText editTextDescription = findViewById(R.id.editTextTopicDescription);
        Button btnBackNav = findViewById(R.id.btnTopicAdderBackNav);
        Button btnAddTopic = findViewById(R.id.btnAddTopic);

        // Create ArrayList with all editTexts for easy validation

        ArrayList<EditText> editTexts = new ArrayList<>(Arrays.asList(
                editTextTopicName,
                editTextYearsOfExperience,
                editTextDescription
        ));

        // Set back navigation and add-topic button on click listeners

        btnBackNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Must send current tutor back to repopulate intent
                Intent intent = new Intent(TopicAdderActivity.this, BACK_NAV_DEST);
                intent.putExtra("Current Tutor", currentTutor);
                startActivity(intent);
            }
        });

        btnAddTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check current number of topics does not exceed max
                if (numTopics == MAX_NUM_TOPICS) {
                    String message1, message2;
                    message1 = "You've already added the maximum number of topics (" + MAX_NUM_TOPICS + ")!";
                    message2 = "Delete a topic to add a new one!";
                    Toast.makeText(TopicAdderActivity.this, message1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(TopicAdderActivity.this, message2, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check all inputs are filled
                if (!RegistrationUtil.validTextInputs(editTexts)) {
                    Toast.makeText(TopicAdderActivity.this,
                            "Please fill all fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create Topic object
                Topic topic = new Topic(null, currentTutor.getId(), editTextTopicName.getText().toString().toLowerCase(),
                        Integer.parseInt(editTextYearsOfExperience.getText().toString()),
                        editTextDescription.getText().toString(), false);

                // Attempt to create topic document
                DBHandler.setDocument(null, TOPIC_COLLECTION, topic, new DBHandler.SetDocumentCallback() {
                    @Override
                    public void onSuccess() {
                        // Toast success (logged in DBHandler)
                        Toast.makeText(TopicAdderActivity.this,
                                "Topic added!", Toast.LENGTH_SHORT).show();

                        // Purge edit texts
                        editTextTopicName.setText(null);
                        editTextYearsOfExperience.setText(null);
                        editTextDescription.setText(null);

                        // Increment number of topics
                        incrementNumTopics();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Toast failure (logged in DBHandler)
                        Toast.makeText(TopicAdderActivity.this,
                                "Failed to add topic, please try again!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // Adds 1 to topic count
    private void incrementNumTopics() {
        numTopics++;
    }
}