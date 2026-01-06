package com.crowdmanagement.crowdmanagementapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class CrowdManagementApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrowdManagementApiApplication.class, args);
    }

}
