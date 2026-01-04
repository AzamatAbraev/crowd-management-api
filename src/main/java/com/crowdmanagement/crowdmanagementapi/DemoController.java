package com.crowdmanagement.crowdmanagementapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo")
public class DemoController {

    @GetMapping("/message")
    public String getDemoMessage() {
        return "This is demo message";
    }
}
