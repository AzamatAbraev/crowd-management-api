package com.crowdmanagement.crowdmanagementapi.timetable;

import com.crowdmanagement.crowdmanagementapi.utils.ApiResponse;
import com.crowdmanagement.crowdmanagementapi.utils.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/resources/")
@CrossOrigin(origins = "*")
public class TimetableController {

    private final TimetableService service;

    public TimetableController(TimetableService service) {
        this.service = service;
    }

    @GetMapping(path = "timetable")
    @PreAuthorize("hasRole('view-timetable')")
    public ResponseEntity<ApiResponse> getTimetable(@RequestParam(required = false) String day,
                                                                @RequestParam(required = false) String className,
                                                                @RequestParam(required = false) String teacher,
                                                                @RequestParam(required = false) String subject,
                                                                @RequestParam(required = false) String classroom,
                                                                @RequestParam(required = false) String startTime,
                                                                @RequestParam(required = false) String endTime

    ) {

        List<TimetableEntry> timetable = service.getFilteredTimetable(day, className, teacher, subject, classroom, startTime, endTime);

        if (timetable.isEmpty()) {
            throw new TimetableNotFoundException("No timetable entries found for the given criteria.");
        }

        return ResponseBuilder.build(HttpStatus.OK, "success", timetable);
    }

    @GetMapping("timetable/metadata")
    @PreAuthorize("hasRole('view-timetable')")
    public ResponseEntity<ApiResponse> getTimetableMetadata() {
        Map<String, List<String>> metadata = service.getTimetableMetadata();
        return ResponseBuilder.build(HttpStatus.OK, "success", metadata);
    }
}
