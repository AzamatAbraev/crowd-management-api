package com.crowdmanagement.crowdmanagementapi.timetable;

import lombok.Data;

import java.util.List;

@Data
public class TimetableResponse {
    private List<TimetableEntry> data;
}
