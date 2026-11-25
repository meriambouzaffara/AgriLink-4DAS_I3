package com.agrilink.usermanagementservice.dto.response;

import com.agrilink.usermanagementservice.enumeration.UserRole;
import com.agrilink.usermanagementservice.enumeration.UserStatus;

public record UserSummaryResponse(
        String keycloakId,
        String firstName,
        String lastName,
        UserRole userRole,
        UserStatus status
) {
}