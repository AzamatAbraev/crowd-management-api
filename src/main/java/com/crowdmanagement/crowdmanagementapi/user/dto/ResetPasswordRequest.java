package com.crowdmanagement.crowdmanagementapi.user.dto;

public record ResetPasswordRequest(
        String newPassword,
        boolean temporary
) {}
