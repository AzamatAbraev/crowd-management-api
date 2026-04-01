package com.crowdmanagement.crowdmanagementapi.timetable;

import lombok.Data;

@Data
@lombok.Builder
public class TimetableEntry {
    private String subject;
    private String className;
    private String teacherName;
    private String groupName;
    private String classroom;
    private String day;
    private String startTime;
    private String endTime;
}

