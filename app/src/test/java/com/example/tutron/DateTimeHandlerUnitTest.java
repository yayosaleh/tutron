package com.example.tutron;

import org.junit.Test;

import static org.junit.Assert.*;

import java.time.LocalDate;

public class DateTimeHandlerUnitTest {

    @Test
    public void addDaysToCurrentDate_isCorrect() {
        int numDays = 5;
        LocalDate expectedDate = LocalDate.now().plusDays(numDays);
        assertEquals(expectedDate, DateTimeHandler.addDaysToCurrentDate(numDays));
    }

    @Test
    public void dateToString_isCorrect() {
        LocalDate date = LocalDate.of(2023, 6, 24);
        String expectedDateString = "24/06/2023";
        assertEquals(expectedDateString, DateTimeHandler.dateToString(date));
    }

    @Test
    public void stringToDate_isCorrect() {
        String dateString = "24/06/2023";
        LocalDate expectedDate = LocalDate.of(2023, 6, 24);
        assertEquals(expectedDate, DateTimeHandler.stringToDate(dateString));
    }

    @Test
    public void isInPastOrPresent_past_isCorrect() {
        LocalDate pastDate = LocalDate.of(1900, 1, 1);
        assertTrue(DateTimeHandler.isInPastOrPresent(pastDate));
    }

    @Test
    public void isInPastOrPresent_present_isCorrect() {
        LocalDate currentDate = LocalDate.now();
        assertTrue(DateTimeHandler.isInPastOrPresent(currentDate));
    }

    @Test
    public void isInPastOrPresent_future_isCorrect() {
        LocalDate futureDate = LocalDate.of(2100, 1, 1);
        assertFalse(DateTimeHandler.isInPastOrPresent(futureDate));
    }

}
