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

    @GetMapping("/hasData")
    public ResponseEntity<AnalyticsService.DataStatus> hasData() {
        return ResponseEntity.ok(analyticsService.getDataStatus());
    }

    @GetMapping("/buildings")
    public List<String> getAvailableBuildings() {
        return analyticsService.getDistinctBuildings();
    }

    @GetMapping("/building/{name}/last7d")
    public List<AnalyticsResponse> getBuildingLast7Days(@PathVariable String name) {
        return analyticsService.getBuildingLast7Days(name);
    }

    @GetMapping("/building/{name}/last30d")
    public List<AnalyticsResponse> getBuildingLast30Days(@PathVariable String name) {
        return analyticsService.getBuildingLast30Days(name);
    }

    @GetMapping("/building/{name}/last90d")
    public List<AnalyticsResponse> getBuildingLast90Days(@PathVariable String name) {
        return analyticsService.getBuildingLast90Days(name);
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
