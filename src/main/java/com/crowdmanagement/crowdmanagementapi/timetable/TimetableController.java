package com.crowdmanagement.crowdmanagementapi.timetable;

import com.crowdmanagement.crowdmanagementapi.utils.ApiResponse;
import com.crowdmanagement.crowdmanagementapi.utils.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/resources/")
@CrossOrigin(origins = "*") // Allows your web app to connect
public class TimetableController {

    private final TimetableService service;

    public TimetableController(TimetableService service) {
        this.service = service;
    }

    @GetMapping(path = "timetable")
    public ResponseEntity<ApiResponse> getTimetable(@RequestParam(required = false) String day,
                                                                @RequestParam(required = false) String className,
                                                                @RequestParam(required = false) String teacher,
                                                                @RequestParam(required = false) String subject,
                                                                @RequestParam(required = false) String classroom) {

        List<TimetableEntry> timetable = service.getFilteredTimetable(day, className, teacher, subject, classroom);

        if (timetable.isEmpty()) {
            throw new TimetableNotFoundException("No timetable entries found for the given criteria.");
        }

        return ResponseBuilder.build(HttpStatus.OK, "success", timetable);
    }
}
