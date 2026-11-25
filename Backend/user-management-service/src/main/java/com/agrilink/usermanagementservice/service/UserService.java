package com.agrilink.usermanagementservice.service;

import com.agrilink.usermanagementservice.dto.request.CreateProfileRequest;
import com.agrilink.usermanagementservice.dto.request.UpdateProfileRequest;
import com.agrilink.usermanagementservice.dto.response.UserResponse;
import com.agrilink.usermanagementservice.dto.response.UserStatisticsResponse;
import com.agrilink.usermanagementservice.dto.response.UserSummaryResponse;
import com.agrilink.usermanagementservice.enumeration.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse createMyProfile(String keycloakId, CreateProfileRequest request);

    UserResponse getMyProfile(String keycloakId);

    UserResponse updateMyProfile(String keycloakId, UpdateProfileRequest request);

    UserResponse getById(Long id);

    UserSummaryResponse getSummary(String keycloakId);

    Page<UserResponse> search(String name, UserRole role, Pageable pageable);

    UserStatisticsResponse statistics();
}