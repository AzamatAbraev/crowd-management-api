package com.crowdmanagement.crowdmanagementapi.timetable;

import lombok.Data;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@lombok.Builder
public class TimetableEntry {
    private String subject;
    private String className;   // e.g., "Level 4 - Group A"
    private String teacherName; // e.g., "John Doe"
    private String groupName;   // e.g., "Seminar Group 1"
    private String classroom;
    private String day;
    private String startTime;
    private String endTime;
}

