package com.example.tutron;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Contains methods used for temporary tutor suspension

public class DateTimeHandler {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Returns date object equal to current date plus numDays
    public static LocalDate addDaysToCurrentDate(int numDays) {
        LocalDate currentDate, newDate;
        currentDate = LocalDate.now();
        newDate = currentDate.plusDays(numDays);
        return newDate;
    }

    // Converts given date object to String in "dd/MM/yyyy" format
    public static String dateToString(LocalDate date) {
        return date.format(formatter);
    }

    // Converts String in "dd/MM/yyyy" format to date object
    public static LocalDate stringToDate(String dateString) {
        return LocalDate.parse(dateString, formatter);
    }

    // Returns true if provided date is in the past
    public static boolean isInPast(LocalDate date) {
        LocalDate currentDate = LocalDate.now();
        int comparison = date.compareTo(currentDate);
        return comparison < 0;
    }

}
