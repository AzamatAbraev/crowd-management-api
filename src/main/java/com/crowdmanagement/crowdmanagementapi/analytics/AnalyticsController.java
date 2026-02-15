package com.crowdmanagement.crowdmanagementapi.analytics;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/resources/")
public class AnalyticsController {

    @GetMapping("analytics")
    public String getData() {
        return "Analytics data";
    }
}
