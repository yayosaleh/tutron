package com.example.tutron;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TopicManagerActivity extends AppCompatActivity {
    private static final String TAG = "TopicManagerActivity";
    private static final String TOPIC_COLLECTION = "topics";
    public static final String TUTOR_COLLECTION = "tutors";
    private static final Class<?>  BACK_NAV_DEST = TutorProfileActivity.class;
    private static final Class<?> ADD_TOPIC_DEST = TopicAdderActivity.class;
    private static final int MAX_NUM_TOPICS = 20;
    private static final int MAX_NUM_OFFERED_TOPICS = 5;

    private Tutor currentTutor;
    private ArrayList<Topic> topicList;
    private ArrayList<Topic> offeredTopicList;
    private GenericRVAdapter<Topic> topicAdapter;

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
                // Ensure topic list has been populated
                if (topicList == null) {
                    Toast.makeText(TopicManagerActivity.this, "Please wait.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Reject if max number of topics is reached (redundant, but provided for better UX)
                if (topicList.size() == MAX_NUM_TOPICS) {
                    String message1, message2;
                    message1 = "You've already added the maximum number of topics (" + MAX_NUM_TOPICS + ")!";
                    message2 = "Delete a topic to add a new one!";
                    Toast.makeText(TopicManagerActivity.this, message1, Toast.LENGTH_SHORT).show();
                    Toast.makeText(TopicManagerActivity.this, message2, Toast.LENGTH_SHORT).show();
                    return;
                }
                // Must send current tutor to repopulate intent upon back navigation (and to access id)
                // Must send current number of topics to limit number of topics added
                Intent intent = new Intent(TopicManagerActivity.this, ADD_TOPIC_DEST);
                intent.putExtra("Current Tutor", currentTutor);
                intent.putExtra("Number of Topics", topicList.size());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Populate topic lists from DB everytime activity is navigated to
        populateLists();
    }

    // Populates topic list and offered topic list
    private void populateLists() {
        // Access shared Firestore database instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Attempt to get topics belonging to current tutor
        db.collection(TOPIC_COLLECTION)
                .whereEqualTo("tutorId", currentTutor.getId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        // Populate topic lists
                        topicList = DBHandler.querySnapshotToList(querySnapshot, Topic.class);
                        offeredTopicList = new ArrayList<>();
                        for (Topic topic : topicList) if (topic.getOffered()) offeredTopicList.add(topic);
                        // TODO: check if tutor offered topic names matches offeredTopicList
                        // Build RecyclerView
                        buildRecyclerView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log and toast failure
                        Log.w(TAG, "Failed to load topics.", e);
                        String message = "Failed to load topics. " + e.getMessage();
                        Toast.makeText(TopicManagerActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Initializes recycler view
    private void buildRecyclerView() {
        // If topic list is empty toast and return
        if (topicList.isEmpty()) {
            Toast.makeText(TopicManagerActivity.this, "You have no topics!", Toast.LENGTH_SHORT).show();
            return;
        }

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
                if (topic.getOffered()) textViewOfferingStatus.setVisibility(View.VISIBLE);
                else textViewOfferingStatus.setVisibility(View.GONE);
                textViewName.setText(capitalizeFirstLetterOfEachWord(topic.getName()));
                String experienceMessage = topic.getYearsOfExperience() + " yr(s)";
                textViewYearsOfExperience.setText(experienceMessage);
                textViewDescription.setText(topic.getDescription());
            }
        };
        // Get RV
        RecyclerView recyclerViewTopics = findViewById(R.id.recyclerViewTopics);
        // Create layout manager -> pass activity
        RecyclerView.LayoutManager topicLayoutManager = new LinearLayoutManager(this);
        //Create adapter -> pass topic list, item layout and binder
        topicAdapter = new GenericRVAdapter<>(topicList, R.layout.topic_item, binder);
        // Set layout manager
        recyclerViewTopics.setLayoutManager(topicLayoutManager);
        // Set adapter
        recyclerViewTopics.setAdapter(topicAdapter);
        // Set on item click listener (toggling topic offering status)
        topicAdapter.setOnItemClickListener(new GenericRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Attempt to update offering status
                updateTopicOfferedStatus(position);
            }
        });
        // Create simple callback for left swipe of RV item (topic deletion)
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // Don't need to implement move functionality in this case
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Get item position
                int position = viewHolder.getAdapterPosition();
                // Attempt to delete topic
                deleteTopic(position);
            }
        };
        // Create item touch helper and attach to RV
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewTopics);
    }

    // Called when RV topic is clicked
    // Attempts to update "offered" field of corresponding DB document
    // Calls updateTutorOfferedTopicNames() if successful
    private void updateTopicOfferedStatus(int position) {
        // Get selected topic
        Topic selectedTopic = topicList.get(position);

        // Reject if selected topic is not offered and tutor has reached max number of offered topics
        if (!selectedTopic.getOffered() && offeredTopicList.size() == MAX_NUM_OFFERED_TOPICS) {
            String message1, message2;
            message1 = "You've already offered the maximum number of topics (" + MAX_NUM_OFFERED_TOPICS + ")! ";
            message2 = "Stop offering one of your topics to start offering a new one!";
            Toast.makeText(TopicManagerActivity.this, message1, Toast.LENGTH_SHORT).show();
            Toast.makeText(TopicManagerActivity.this, message2, Toast.LENGTH_SHORT).show();
            return;
        }

        // Copy selected topic and invert offered status (since document-setting might fail)
        Topic selectedTopicCopy = new Topic(selectedTopic.getId(), selectedTopic.getTutorId(),
                selectedTopic.getName(), selectedTopic.getYearsOfExperience(),
                selectedTopic.getDescription(), !selectedTopic.getOffered());

        // Attempt to update topic document in DB
        DBHandler.setDocument(selectedTopicCopy.getId(), TOPIC_COLLECTION, selectedTopicCopy, new DBHandler.SetDocumentCallback() {
            @Override
            public void onSuccess() {
                // Update offered topic list and formulate success message
                String message;
                if (selectedTopic.getOffered()) { // Selected topic was offered -> stop offering
                    offeredTopicList.remove(selectedTopic); // offeredTopicList maintains references to a subset of objects in topicList!
                    message = "Topic no longer offered!";
                } else { // Selected topic was not offered -> start offering
                    offeredTopicList.add(selectedTopicCopy);
                    message = "Topic now offered!";
                }

                // Update topic list and notify RV adapter of change
                topicList.set(position, selectedTopicCopy);
                topicAdapter.notifyItemChanged(position);

                // Toast success (logged in DBHandler)
                Toast.makeText(TopicManagerActivity.this, message, Toast.LENGTH_SHORT).show();

                // Call updateTutorOfferedTopicNames() to update tutor document in DB
                updateTutorOfferedTopicNames();
            }

            @Override
            public void onFailure(Exception e) {
                // No DB document was updated, and no instance variables were modified -> no req. cleanup
                // Toast failure (logged in DBHandler)
                String message = "Failed to update topic! Please try again.";
                Toast.makeText(TopicManagerActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteTopic(int position) {
        // Get selected topic
        Topic selectedTopic = topicList.get(position);

        // Attempt to delete topic document from DB
        DBHandler.deleteDocument(selectedTopic.getId(), TOPIC_COLLECTION, new DBHandler.SetDocumentCallback() {
                    @Override
                    public void onSuccess() {
                        // Update offered topic list and tutor document if deleted topic was offered
                        if (selectedTopic.getOffered()) {
                            offeredTopicList.remove(selectedTopic);
                            updateTutorOfferedTopicNames();
                        }

                        // Update topic list and notify RV adapter of change
                        topicList.remove(position);
                        topicAdapter.notifyItemRemoved(position);

                        // Toast success (logged in DBHandler)
                        Toast.makeText(TopicManagerActivity.this, "Topic deleted!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // No DB document was deleted, and no instance variables were modified -> no req. cleanup
                        // Toast failure (logged in DBHandler)
                        String message = "Failed to delete topic! Please try again.";
                        Toast.makeText(TopicManagerActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateTutorOfferedTopicNames() {
        // Generate list of offered topic names
        // Note: offeredTopicList is always the true list of offered topics from the DB!
        ArrayList<String> offeredTopicNames = new ArrayList<>();
        for (Topic topic : offeredTopicList) offeredTopicNames.add(topic.getName());

        // Copy current tutor and update list of offered topic names
        Tutor currentTutorCopy = new Tutor(currentTutor.getId(), currentTutor.getFirstName(),
                currentTutor.getLastName(), currentTutor.getEducationLevel(),
                currentTutor.getNativeLanguage(), currentTutor.getDescription(), currentTutor.getProfilePic());
        currentTutorCopy.setOfferedTopicNames(offeredTopicNames);

        // Attempt to update tutor document in DB
        DBHandler.setDocument(currentTutorCopy.getId(), TUTOR_COLLECTION, currentTutorCopy, new DBHandler.SetDocumentCallback() {
            @Override
            public void onSuccess() {
                // We don't notify user of success (seamless), only of failure (sync error)
                // Update current tutor object
                currentTutor = currentTutorCopy;
            }

            @Override
            public void onFailure(Exception e) {
                // Toast failure (logged in DBHandler)
                String message1, message2;
                message1  = "Failed to sync offered topics with profile!";
                message2  = "Please tap any topic to resync!";
                Toast.makeText(TopicManagerActivity.this, message1, Toast.LENGTH_SHORT).show();
                Toast.makeText(TopicManagerActivity.this, message2, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String capitalizeFirstLetterOfEachWord(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        boolean capitalizeNextChar = true;

        for (char ch : input.toCharArray()) {
            if (Character.isWhitespace(ch)) {
                capitalizeNextChar = true;
            } else if (capitalizeNextChar) {
                ch = Character.toTitleCase(ch);
                capitalizeNextChar = false;
            }

            result.append(ch);
        }

        return result.toString();
    }
}