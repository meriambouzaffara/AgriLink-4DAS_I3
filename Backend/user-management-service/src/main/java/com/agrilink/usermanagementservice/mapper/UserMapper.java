package com.agrilink.usermanagementservice.mapper;

import com.agrilink.usermanagementservice.dto.response.UserResponse;
import com.agrilink.usermanagementservice.dto.response.UserSummaryResponse;
import com.agrilink.usermanagementservice.entity.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User u) {
        return new UserResponse(
                u.getId(),
                u.getKeycloakId(),
                u.getFirstName(),
                u.getLastName(),
                u.getPhone(),
                u.getAddress(),
                u.getUserRole(),
                u.getStatus(),
                u.getCreatedAt(),
                u.getUpdatedAt()
        );
    }

    public static UserSummaryResponse toSummary(User u) {
        return new UserSummaryResponse(
                u.getKeycloakId(),
                u.getFirstName(),
                u.getLastName(),
                u.getUserRole(),
                u.getStatus()
        );
    }
}