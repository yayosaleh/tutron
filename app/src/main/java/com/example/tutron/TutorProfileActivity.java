package com.example.tutron;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TutorProfileActivity extends AppCompatActivity {
    private static final Class<?> MANAGE_TOPICS_DEST = TopicManagerActivity.class;
    private static final String TIMESLOT_COLLECTION = "timeslots";

    private Tutor currentTutor;
    private ArrayList<Timeslot> timeslotList;

    // Timeslot handling
    private Calendar pickedDate;
    private Calendar pickedStartTime;
    private Calendar pickedEndTime;
    private Button btnSetDate;
    private Button btnSetStartTime;
    private Button btnSetEndTime;

    // Nested class for Date Picker Dialog
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        private Button buttonToUpdate;
        private Calendar pickedDate;

        public DatePickerFragment(Button buttonToUpdate, Calendar pickedDate) {
            this.buttonToUpdate = buttonToUpdate;
            this.pickedDate = pickedDate;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            return dialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            pickedDate.set(year, month, dayOfMonth);
            buttonToUpdate.setText(String.format(Locale.getDefault(), "%tF", pickedDate.getTime()));
        }
    }

    // Nested class for Time Picker Dialog
    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        private Button buttonToUpdate;
        private Calendar pickedTime;

        public TimePickerFragment(Button buttonToUpdate, Calendar pickedTime) {
            this.buttonToUpdate = buttonToUpdate;
            this.pickedTime = pickedTime;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            TimePickerDialog dialog = new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
            return dialog;
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            pickedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            pickedTime.set(Calendar.MINUTE, minute);
            buttonToUpdate.setText(String.format(Locale.getDefault(), "%tT", pickedTime.getTime()));
        }
    }

    private void updateCurrentTutor() {
        currentTutor = DataManager.getInstance().getCurrentTutor();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_profile);

        // Get current tutor
        updateCurrentTutor();

        // Bind current tutor to reusable tutor item view
        View tutorItemView = findViewById(R.id.tutorItem);
        currentTutor.bindToView(tutorItemView);

        // Initialize picked date and times
        pickedDate = Calendar.getInstance();
        pickedStartTime = Calendar.getInstance();
        pickedEndTime = Calendar.getInstance();

        // Initialize hourly rate edit text and set hint to current hourly rate
        EditText editTextHourlyRate = findViewById(R.id.editTextHourlyRate);
        editTextHourlyRate.setText(String.valueOf(currentTutor.getHourlyRate()));

        // Initialize button variables
        Button btnLogOff = findViewById(R.id.btnTutorProfileLogOff);
        Button btnManageTopics = findViewById(R.id.btnManageTopics);
        Button btnUpdateHourlyRate = findViewById((R.id.btnUpdateHourlyRate));
        btnSetDate = findViewById(R.id.btnSetDate);
        btnSetStartTime = findViewById(R.id.btnSetStartTime);
        btnSetEndTime = findViewById(R.id.btnSetEndTime);
        Button btnAddTimeSlot = findViewById(R.id.btnAddTimeSlot);

        // Set on click listeners for log off and manage topics buttons
        btnLogOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call log off method from util class
                AuthUtil.signOut(TutorProfileActivity.this);
            }
        });

        btnManageTopics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TutorProfileActivity.this, MANAGE_TOPICS_DEST);
                startActivity(intent);
            }
        });

        // Set on click listener for hourly rate update button
        btnUpdateHourlyRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check editText is not empty
                if (editTextHourlyRate.getText().toString().isEmpty()) {
                    Toast.makeText(TutorProfileActivity.this,
                            "Please enter in a hourly rate ($/hr)!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Copy current tutor and update hourly rate
                Tutor tutorCopy = currentTutor.copy();
                tutorCopy.setHourlyRate(Double.parseDouble(editTextHourlyRate.getText().toString()));

                // Attempt to update tutor document in DB
                DBHandler.setDocument(tutorCopy.getId(), DBHandler.TUTOR_COLLECTION, tutorCopy, new DBHandler.SetDocumentCallback() {
                    @Override
                    public void onSuccess() {
                        // Toast success
                        Toast.makeText(TutorProfileActivity.this,
                                "Hourly rate updated!", Toast.LENGTH_SHORT).show();
                        // Set current tutor to tutor copy
                        currentTutor = tutorCopy;
                        // Rebind tutor item view
                        currentTutor.bindToView(tutorItemView);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Toast failure
                        Toast.makeText(TutorProfileActivity.this,
                                "Failed to update hourly rate, please try again!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        // Set on click listeners for date and time picking buttons
        btnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment(btnSetDate, pickedDate);
                datePickerFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        btnSetStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePickerFragment = new TimePickerFragment(btnSetStartTime, pickedStartTime);
                timePickerFragment.show(getSupportFragmentManager(), "startTimePicker");
            }
        });

        btnSetEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePickerFragment = new TimePickerFragment(btnSetEndTime, pickedEndTime);
                timePickerFragment.show(getSupportFragmentManager(), "endTimePicker");
            }
        });

        // Set on click listener for add timeslot button
        btnAddTimeSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get start and end times (dates)
                pickedDate.set(Calendar.HOUR_OF_DAY, pickedStartTime.get(Calendar.HOUR_OF_DAY));
                pickedDate.set(Calendar.MINUTE, pickedStartTime.get(Calendar.MINUTE));
                Date startTime = pickedDate.getTime();

                pickedDate.set(Calendar.HOUR_OF_DAY, pickedEndTime.get(Calendar.HOUR_OF_DAY));
                pickedDate.set(Calendar.MINUTE, pickedEndTime.get(Calendar.MINUTE));
                Date endTime = pickedDate.getTime();

                // Check startTime is in the future
                if (startTime.before(new Date())) {
                    Toast.makeText(TutorProfileActivity.this,
                            "Start time must be in the future!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check that startTime is earlier than endTime (i.e., this is a possible range)
                if (!isValidTimeslot(startTime, endTime)) {
                    Toast.makeText(TutorProfileActivity.this,
                            "Start and end times are not possible!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check the proposed timeslot does not conflict with any of the preexistent timeslots
                if (conflictsWithExistingTimeslots(startTime, endTime)) {
                    Toast.makeText(TutorProfileActivity.this,
                            "This timeslot conflicts with existing timeslot!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //  Construct a Timeslot instance
                Timeslot newTimeslot = new Timeslot(null, currentTutor.getId(), startTime, endTime);

                // Attempt to create document in DB
                DBHandler.setDocument(null, TIMESLOT_COLLECTION, newTimeslot, new DBHandler.SetDocumentCallback() {
                    @Override
                    public void onSuccess() {
                        // Toast success
                        Toast.makeText(TutorProfileActivity.this, "Timeslot added!", Toast.LENGTH_SHORT).show();
                        // Update timeslotList
                        timeslotList.add(newTimeslot);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Toast failure
                        Toast.makeText(TutorProfileActivity.this, "Failed to add timeslot, please try again!",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Update current tutor (needed for back navigation)
        updateCurrentTutor();

        // Populate/update time slot list
        updateTimeslots();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Set current tutor for use by downstream activities
        DataManager.getInstance().setCurrentTutor(currentTutor);
    }

    private void updateTimeslots() {
        // Create list with one query condition
        ArrayList<DBHandler.QueryCondition> conditions = new ArrayList<>();
        conditions.add(new DBHandler.QueryCondition("tutorId", "==", currentTutor.getId()));

        // Attempt to perform query
        DBHandler.performQuery(TIMESLOT_COLLECTION, Timeslot.class, conditions, new DBHandler.QueryCallback<Timeslot>() {
            @Override
            public void onSuccess(ArrayList<Timeslot> items) {
                timeslotList = items;
            }

            @Override
            public void onFailure(Exception e) {
                //TODO: handle
            }
        });
    }

    private boolean isValidTimeslot(Date startTime, Date endTime) {
        if (startTime == null || endTime == null) return false;
        return startTime.before(endTime);
    }

    private boolean conflictsWithExistingTimeslots(Date startTime, Date endTime) {
        if (timeslotList != null) {
            for (Timeslot timeslot : timeslotList) {
                if (timeslot.getStartTime().before(endTime) && timeslot.getEndTime().after(startTime)) {
                    return true;
                }
            }
        }
        return false;
    }
}