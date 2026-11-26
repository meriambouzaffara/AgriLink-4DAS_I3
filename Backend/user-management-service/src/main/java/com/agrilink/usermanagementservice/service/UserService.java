
package com.agrilink.usermanagementservice.service;

import com.agrilink.usermanagementservice.dto.request.AdminCreateUserRequest;
import com.agrilink.usermanagementservice.dto.request.CreateProfileRequest;
import com.agrilink.usermanagementservice.dto.request.UpdateProfileRequest;

import com.agrilink.usermanagementservice.dto.response.UserResponse;
import com.agrilink.usermanagementservice.dto.response.UserStatisticsResponse;
import com.agrilink.usermanagementservice.dto.response.UserSummaryResponse;

import com.agrilink.usermanagementservice.enumeration.UserRole;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface UserService {

    // Inscription personnelle
    UserResponse createMyProfile(
            String keycloakId,
            Set<String> callerRoles,
            CreateProfileRequest request
    );

    // Consultation et modification du profil personnel
    UserResponse getMyProfile(String keycloakId);

    UserResponse updateMyProfile(
            String keycloakId,
            UpdateProfileRequest request
    );

    // Administration
    UserResponse createManagedUser(AdminCreateUserRequest request);

    UserResponse getById(Long id);

    void deleteUser(Long id, String currentAdminKeycloakId);

    Page<UserResponse> search(
            String name,
            UserRole role,
            Pageable pageable
    );

    UserStatisticsResponse statistics();

    // Communication avec les autres microservices
    UserSummaryResponse getSummary(String keycloakId);
}
