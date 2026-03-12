package com.crowdmanagement.crowdmanagementapi.iot;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/people")
public class PeopleCountController {

    private final PeopleCountService peopleCountService;

    public PeopleCountController(PeopleCountService peopleCountService) {
        this.peopleCountService = peopleCountService;
    }

    @GetMapping(path = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('view-occupancy')")
    public Map<String, Object> getLatestCount() {
        return Map.of(
                "count",        peopleCountService.getCurrentCount(),
                "lastDevice",   peopleCountService.getLastDeviceName(),
                "systemStatus", "ACTIVE",
                "activeNodes",  peopleCountService.getDeviceCounts().size(),
                "deviceCounts", peopleCountService.getDeviceCounts()
        );
    }


    @PostMapping("/reset")
    @PreAuthorize("hasRole('reset-occupancy')")
    public Map<String, String> resetCount() {
        peopleCountService.reset();
        return Map.of("message", "Counter reset to 0 successfully");
    }
}
