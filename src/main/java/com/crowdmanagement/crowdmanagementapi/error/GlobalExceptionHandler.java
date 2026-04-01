package com.crowdmanagement.crowdmanagementapi.error;

import com.crowdmanagement.crowdmanagementapi.utils.ApiResponse;
import com.crowdmanagement.crowdmanagementapi.timetable.TimetableNotFoundException;
import com.crowdmanagement.crowdmanagementapi.utils.ResponseBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse> handleNotSupportedMethod(HttpRequestMethodNotSupportedException ex) {
        var response = new ApiResponse();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
        response.setCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        response.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.ALLOW, "GET").contentType(MediaType.APPLICATION_JSON).body(response);
    }

    @ExceptionHandler(TimetableNotFoundException.class)
    public ResponseEntity<ApiResponse> handleNotFound(TimetableNotFoundException ex) {
        var response = new ApiResponse();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.NOT_FOUND);
        response.setCode(HttpStatus.NOT_FOUND.value());
        response.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGlobalException(Exception ex) {
        var response = new ApiResponse();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(response);
    }
}