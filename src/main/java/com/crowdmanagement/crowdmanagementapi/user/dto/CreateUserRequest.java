package com.crowdmanagement.crowdmanagementapi.user.dto;

public record CreateUserRequest(
        String username,
        String firstName,
        String lastName,
        String email,
        String password,
        boolean temporaryPassword
) {}
