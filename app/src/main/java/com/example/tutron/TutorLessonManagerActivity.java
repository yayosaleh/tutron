package com.example.tutron;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class TutorLessonManagerActivity extends AppCompatActivity {
    private static final Class<?> BACK_NAV_DEST = TutorProfileActivity.class;
    private static final String LESSON_COLLECTION = "lessons";
    private static final String TIMESLOT_COLLECTION = "timeslots";
    private Tutor currentTutor;
    private ArrayList<Lesson> lessonList;
    private GenericRVAdapter<Lesson> lessonAdapter;

    private void updateCurrentTutor() {
        currentTutor = DataManager.getInstance().getCurrentTutor();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_lesson_manager);

        // Get current tutor
        updateCurrentTutor();

        // Set on click listener for back navigation button
        Button btnBackNav = findViewById(R.id.btnTutorLessonManagerBackNav);
        btnBackNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TutorLessonManagerActivity.this, BACK_NAV_DEST);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Update current tutor
        updateCurrentTutor();

        // Populate lesson list
        populateList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Set current tutor for downstream activities
        DataManager.getInstance().setCurrentTutor(currentTutor);
    }

    // Populates list of lessons
    private void populateList() {
        // Define query conditions
        // Here, we only want to fetch non-rejected lessons
        ArrayList<DBHandler.QueryCondition> conditions = new ArrayList<>();
        conditions.add(new DBHandler.QueryCondition("tutorId", "==", currentTutor.getId()));
        conditions.add(new DBHandler.QueryCondition("status", ">", -1));

        // Perform query
        DBHandler.performQuery(LESSON_COLLECTION, Lesson.class, conditions, new DBHandler.QueryCallback<Lesson>() {
            @Override
            public void onSuccess(ArrayList<Lesson> items) {
                // If there are no lessons, toast and return
                if (items.isEmpty()) {
                    Toast.makeText(TutorLessonManagerActivity.this,
                            "You have no lessons or lesson requests.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Build recycler view
                lessonList = items;
                buildRecyclerView();
            }
            @Override
            public void onFailure(Exception e) {
                // Toast failure
                Toast.makeText(TutorLessonManagerActivity.this,
                        "Failed to get your lessons, please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildRecyclerView () {
        // Define RV bindings for adapter
        GenericRVAdapter.Binder<Lesson> binder = new GenericRVAdapter.Binder<Lesson>() {
            @Override
            public void bindData(Lesson lesson, GenericRVAdapter.GenericViewHolder holder) {
                // Get the whole lesson_item view from the holder
                View lessonItemView = holder.itemView;
                // Create copy of lesson to hide tutor name
                Lesson lessonCopy = lesson.copy();
                lessonCopy.setTutorName("");
                // Bind lesson to the view
                lessonCopy.bindToView(lessonItemView);
            }
        };

        // Get RecyclerView
        RecyclerView recyclerViewLessons = findViewById(R.id.recyclerViewTutorLessons);
        // Create layout manager
        RecyclerView.LayoutManager lessonLayoutManager = new LinearLayoutManager(this);
        // Create adapter
        lessonAdapter = new GenericRVAdapter<>(lessonList, R.layout.lesson_item, binder);
        // Set layout manager
        recyclerViewLessons.setLayoutManager(lessonLayoutManager);
        // Set adapter
        recyclerViewLessons.setAdapter(lessonAdapter);

        // Set on item click listener to approve lesson
        lessonAdapter.setOnItemClickListener(new GenericRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // If selected lesson is not pending, toast and return
                if (lessonList.get(position).getStatus() != 0) {
                    Toast.makeText(TutorLessonManagerActivity.this,
                            "This lesson has already been accepted!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Copy lesson and update status to approved
                Lesson lessonCopy  = lessonList.get(position).copy();
                lessonCopy.setStatus(1);

                // Attempt to update lesson in DB
                DBHandler.setDocument(lessonCopy.getId(), LESSON_COLLECTION, lessonCopy, new DBHandler.SetDocumentCallback() {
                    @Override
                    public void onSuccess() {
                        // Update lessonList with approved lesson
                        lessonList.set(position, lessonCopy);
                        lessonAdapter.notifyItemChanged(position);

                        // Copy current tutor and increment number of lessons
                        Tutor currentTutorCopy = currentTutor.copy();
                        int numLessonsGiven = currentTutorCopy.getNumLessonsGiven() + 1;
                        currentTutorCopy.setNumLessonsGiven(numLessonsGiven);

                        // Attempt to update tutor document
                        DBHandler.setDocument(currentTutorCopy.getId(), DBHandler.TUTOR_COLLECTION, currentTutorCopy, new DBHandler.SetDocumentCallback() {
                            @Override
                            public void onSuccess() {
                                // Set current tutor
                                currentTutor = currentTutorCopy;
                            }
                            @Override
                            public void onFailure(Exception e) {
                                //TODO: handle
                            }
                        });

                        // Attempt to update timeslot document status to booked
                        Timeslot timeslot = new Timeslot(lessonCopy.getTimeslotId(), currentTutorCopy.getId(), lessonCopy.getStartTime(), lessonCopy.getEndTime());
                        DBHandler.setDocument(lessonCopy.getTimeslotId(), TIMESLOT_COLLECTION, timeslot, new DBHandler.SetDocumentCallback() {
                            @Override
                            public void onSuccess() {
                                // Do nothing
                            }
                            @Override
                            public void onFailure(Exception e) {
                                // Toast failure
                                Toast.makeText(TutorLessonManagerActivity.this,
                                        "Failed to set timeslot to booked, you may receive more requests for this timeslot.", Toast.LENGTH_SHORT).show();
                            }
                        });

                        // Attempt to reject all pending requests with the same timeslot
                        rejectLessonsWithSameTimeslot(lessonCopy.getTimeslotId());
                    }
                    @Override
                    public void onFailure(Exception e) {
                        // Toast failure
                        Toast.makeText(TutorLessonManagerActivity.this,
                                "Failed to approve lesson.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Create simple callback for left swipe of RV item (request rejection)
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // Don't need to implement move functionality in this case
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Get item position and selected lesson
                int position = viewHolder.getAdapterPosition();
                Lesson selectedLesson = lessonList.get(position);

                // Attempt to reject request
                rejectLesson(selectedLesson);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                // Get item position and selected lesson
                int position = viewHolder.getAdapterPosition();
                Lesson selectedLesson = lessonList.get(position);

                // If selected lesson is not pending, toast and return 0 (i.e., block swipe)
                if (selectedLesson.getStatus() != 0) {
//                    Toast.makeText(TutorLessonManagerActivity.this,
//                            "You can't reject a lesson you've already accepted!", Toast.LENGTH_SHORT).show();
                    return 0;
                }

                return super.getSwipeDirs(recyclerView, viewHolder);
            }
        };
        // Create item touch helper and attach to RV
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewLessons);
    }

    // Updates lesson document in DB
    private void rejectLesson(Lesson lesson) {
        // Create copy of lesson and update status to rejected
        Lesson lessonCopy  = lesson.copy();
        lessonCopy.setStatus(-1);
        // Update lesson document
        DBHandler.setDocument(lessonCopy.getId(), LESSON_COLLECTION, lessonCopy, new DBHandler.SetDocumentCallback() {
            @Override
            public void onSuccess() {
                // Toast success
                Toast.makeText(TutorLessonManagerActivity.this,
                        "Lesson rejected.", Toast.LENGTH_SHORT).show();
                // Remove lesson from lesson list and notify RV
                removeFromLessonList(lessonCopy.getId());
            }
            @Override
            public void onFailure(Exception e) {
                // Toast failure
                Toast.makeText(TutorLessonManagerActivity.this,
                        "Failed to reject lesson.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Removes lesson from lesson list and notifies RV
    // Receives ID so removal is not object-dependant
    private void removeFromLessonList(String lessonId) {
        for (int i = 0; i < lessonList.size(); i++) {
            Lesson lesson = lessonList.get(i);
            if (lesson.getId().equals(lessonId)) {
                lessonList.remove(i);
                lessonAdapter.notifyItemRemoved(i);
                return;
            }
        }
    }

    // Rejects all of the pending lessons with the same timeslot as an approved lesson
    private void rejectLessonsWithSameTimeslot(String timeslotId) {
        // Define query conditions
        ArrayList<DBHandler.QueryCondition> conditions = new ArrayList<>();
        conditions.add(new DBHandler.QueryCondition("tutorId", "==", currentTutor.getId()));
        conditions.add(new DBHandler.QueryCondition("status", "==", 0)); // Pending status
        conditions.add(new DBHandler.QueryCondition("timeslotId", "==", timeslotId));

        // Perform query
        DBHandler.performQuery(LESSON_COLLECTION, Lesson.class, conditions, new DBHandler.QueryCallback<Lesson>() {
            @Override
            public void onSuccess(ArrayList<Lesson> items) {
                // Reject each lesson in list
                // This won't reject lesson we just approved since it is no longer pending!
                for (Lesson lesson: items) rejectLesson(lesson);
            }
            @Override
            public void onFailure(Exception e) {
                // Toast failure
                Toast.makeText(TutorLessonManagerActivity.this,
                        "Failed to reject lesson requests for the same timeslot as the lesson you just approved. Please do so yourself!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}