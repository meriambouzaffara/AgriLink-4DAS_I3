package com.agrilink.usermanagementservice.dto.response;

import com.agrilink.usermanagementservice.enumeration.UserRole;
import java.util.Map;

public record UserStatisticsResponse(
        long totalUsers,
        Map<UserRole, Long> byRole
) {
}