package com.crowdmanagement.crowdmanagementapi.user.dto;

import java.util.List;

public record UserRepresentationDto(
        String id,
        String username,
        String firstName,
        String lastName,
        String email,
        boolean enabled,
        List<String> roles
) {}
