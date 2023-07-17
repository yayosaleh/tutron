package com.example.tutron;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Date;

public class TimeslotUnitTest {

    @Test
    public void isValidTimeslot_returnsTrue() {
        Date startTime = new Date(System.currentTimeMillis() - 1000);
        Date endTime = new Date();
        assertTrue(TutorProfileActivity.isValidTimeslot(startTime, endTime));
    }

    @Test
    public void isValidTimeslot_returnsFalse_whenStartTimeIsNull() {
        Date endTime = new Date();
        assertFalse(TutorProfileActivity.isValidTimeslot(null, endTime));
    }

    @Test
    public void isValidTimeslot_returnsFalse_whenEndTimeIsNull() {
        Date startTime = new Date();
        assertFalse(TutorProfileActivity.isValidTimeslot(startTime, null));
    }

    @Test
    public void isValidTimeslot_returnsFalse_whenEndTimeIsBeforeStartTime() {
        Date startTime = new Date();
        Date endTime = new Date(System.currentTimeMillis() - 1000);
        assertFalse(TutorProfileActivity.isValidTimeslot(startTime, endTime));
    }

    @Test
    public void conflictsWithExistingTimeslots_returnsFalse_whenTimeslotListIsNull() {
        Date startTime = new Date(System.currentTimeMillis() - 1000);
        Date endTime = new Date();
        assertFalse(TutorProfileActivity.conflictsWithExistingTimeslots(null, startTime, endTime));
    }

    @Test
    public void conflictsWithExistingTimeslots_returnsTrue_whenNewTimeslotConflicts() {
        // Set up overlapping timeslots
        Date startTime1 = new Date(System.currentTimeMillis() - 2000);
        Date endTime1 = new Date(System.currentTimeMillis() - 1000);
        Date startTime2 = new Date(System.currentTimeMillis() - 1500);
        Date endTime2 = new Date();
        Timeslot timeslot = new Timeslot(null, null, startTime1, endTime1);
        ArrayList<Timeslot> timeslotList = new ArrayList<>();
        timeslotList.add(timeslot);
        assertTrue(TutorProfileActivity.conflictsWithExistingTimeslots(timeslotList, startTime2, endTime2));
    }

    @Test
    public void conflictsWithExistingTimeslots_returnsFalse_whenNoConflict() {
        // Set up non-overlapping timeslots
        Date startTime1 = new Date(System.currentTimeMillis() - 2000);
        Date endTime1 = new Date(System.currentTimeMillis() - 1000);
        Date startTime2 = new Date();
        Date endTime2 = new Date(System.currentTimeMillis() + 1000);
        Timeslot timeslot = new Timeslot(null, null, startTime1, endTime1);
        ArrayList<Timeslot> timeslotList = new ArrayList<>();
        timeslotList.add(timeslot);
        assertFalse(TutorProfileActivity.conflictsWithExistingTimeslots(timeslotList, startTime2, endTime2));
    }
}