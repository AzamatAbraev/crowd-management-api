package com.crowdmanagement.crowdmanagementapi.timetable;

public class TimetableNotFoundException extends RuntimeException {
    public TimetableNotFoundException(String message) {
        super(message);
    }
}
