package com.agrilink.usermanagementservice.dto.response;

import com.agrilink.usermanagementservice.enumeration.UserRole;
import com.agrilink.usermanagementservice.enumeration.UserStatus;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String keycloakId,
        String firstName,
        String lastName,
        String phone,
        String address,
        UserRole userRole,
        UserStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}