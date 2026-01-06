package com.crowdmanagement.crowdmanagementapi.timetable;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/timetable")
@CrossOrigin(origins = "*") // Allows your web app to connect
public class TimetableController {

    private final TimetableService service;

    public TimetableController(TimetableService service) {
        this.service = service;
    }

    @GetMapping("")
    public List<TimetableEntry> getTimetable(@RequestParam(required = false) String day,
                                               @RequestParam(required = false) String className,
                                               @RequestParam(required = false) String teacher,
                                               @RequestParam(required = false) String subject,
                                               @RequestParam(required = false) String classroom) {
        try {
            return service.getFilteredTimetable(day, className, teacher, subject, classroom);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse timetable: " + e.getMessage());
        }
    }
}
