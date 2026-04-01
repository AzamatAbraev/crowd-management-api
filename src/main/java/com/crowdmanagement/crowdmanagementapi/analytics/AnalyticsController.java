package com.crowdmanagement.crowdmanagementapi.analytics;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping(path = "/building/{name}/today", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AnalyticsResponse>> getBuildingToday(@PathVariable String name) {
        return ResponseEntity.ok(analyticsService.getBuildingToday(name));
    }

    @GetMapping("/building/{name}/week")
    public List<AnalyticsResponse> getBuildingWeek(@PathVariable String name) {
        return analyticsService.getBuildingWeek(name);
    }

    @GetMapping("/building/{name}/year")
    public List<AnalyticsResponse> getBuildingYear(@PathVariable String name) {
        return analyticsService.getBuildingYear(name);
    }


    @GetMapping("/room/{name}/today")
    public List<AnalyticsResponse> getRoomToday(@PathVariable String name) {
        return analyticsService.getRoomToday(name);
    }

    @GetMapping("/room/{name}/week")
    public List<AnalyticsResponse> getRoomWeek(@PathVariable String name) {
        return analyticsService.getRoomWeek(name);
    }

    @GetMapping("/room/{name}/year")
    public List<AnalyticsResponse> getRoomYear(@PathVariable String name) {
        return analyticsService.getRoomYear(name);
    }
}
