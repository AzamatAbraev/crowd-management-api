package com.crowdmanagement.crowdmanagementapi.user.dto;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        String email
) {}
