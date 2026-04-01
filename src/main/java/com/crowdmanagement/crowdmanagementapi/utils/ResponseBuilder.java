package com.crowdmanagement.crowdmanagementapi.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class ResponseBuilder {
    public static ResponseEntity<ApiResponse> build(HttpStatus status, String message, Object data) {
        var response = new ApiResponse();
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(status);
        response.setCode(status.value());
        response.setData(data);

        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(response);
    }
}
