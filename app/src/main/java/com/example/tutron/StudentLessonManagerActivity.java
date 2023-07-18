package com.example.tutron;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

public class StudentLessonManagerActivity extends AppCompatActivity {
    private static final Class<?> BACK_NAV_DEST = StudentHomeActivity.class;
    private static final Class<?> ON_ITEM_CLICK_DEST = SelectedLessonActivity.class;
    private static final String LESSON_COLLECTION = "lessons";
    private Student currentStudent;
    private ArrayList<Lesson> lessonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_lesson_manager);

        // Get current student
        currentStudent = DataManager.getInstance().getCurrentStudent();

        // Set on click listener for back navigation button
        Button btnBackNav = findViewById(R.id.btnStudentLessonManagerBackNav);
        btnBackNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentLessonManagerActivity.this, BACK_NAV_DEST);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Populate lesson list
        populateList();
    }

    private void populateList() {
        // Define query conditions
        ArrayList<DBHandler.QueryCondition> conditions = new ArrayList<>();
        conditions.add(new DBHandler.QueryCondition("studentId", "==", currentStudent.getId()));

        // Perform query
        DBHandler.performQuery(LESSON_COLLECTION, Lesson.class, conditions, new DBHandler.QueryCallback<Lesson>() {
            @Override
            public void onSuccess(ArrayList<Lesson> items) {
                // If there are no lessons, toast and return
                if (items.isEmpty()) {
                    Toast.makeText(StudentLessonManagerActivity.this,
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
                Toast.makeText(StudentLessonManagerActivity.this,
                        "Failed to get your lessons, please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildRecyclerView() {
        // Define RV bindings for adapter
        GenericRVAdapter.Binder<Lesson> binder = new GenericRVAdapter.Binder<Lesson>() {
            @Override
            public void bindData(Lesson lesson, GenericRVAdapter.GenericViewHolder holder) {
                // Get the whole lesson_item view from the holder
                View lessonItemView = holder.itemView;
                // Create copy of lesson to hide tutor name
                Lesson lessonCopy = lesson.copy();
                lessonCopy.setStudentName("");
                // Bind lesson to the view
                lessonCopy.bindToView(lessonItemView);
            }
        };

        // Get RecyclerView
        RecyclerView recyclerViewLessons = findViewById(R.id.recyclerViewStudentLessons);
        // Create layout manager
        RecyclerView.LayoutManager lessonLayoutManager = new LinearLayoutManager(this);
        // Create adapter
        GenericRVAdapter<Lesson> lessonAdapter = new GenericRVAdapter<>(lessonList, R.layout.lesson_item, binder);
        // Set layout manager
        recyclerViewLessons.setLayoutManager(lessonLayoutManager);
        // Set adapter
        recyclerViewLessons.setAdapter(lessonAdapter);
        // Set on item click listener to action a lesson (review or complain)
        lessonAdapter.setOnItemClickListener(new GenericRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // If lesson isn't completed, toast and return
                Lesson selectedLesson = lessonList.get(position);
                if (selectedLesson.getStatus() != 1 || selectedLesson.getEndTime().after(new Date())) {
                    Toast.makeText(StudentLessonManagerActivity.this,
                            "You can only review or complain about completed lessons.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Navigate to selected lesson activity and send selected lesson
                Intent intent = new Intent(StudentLessonManagerActivity.this, ON_ITEM_CLICK_DEST);
                intent.putExtra("Selected Lesson", selectedLesson);
                startActivity(intent);
            }
        });
    }
}