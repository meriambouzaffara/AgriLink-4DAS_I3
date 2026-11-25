package com.agrilink.usermanagementservice.controller;

import com.agrilink.usermanagementservice.dto.request.CreateProfileRequest;
import com.agrilink.usermanagementservice.dto.request.UpdateProfileRequest;
import com.agrilink.usermanagementservice.dto.response.PageResponse;
import com.agrilink.usermanagementservice.dto.response.UserResponse;
import com.agrilink.usermanagementservice.dto.response.UserStatisticsResponse;
import com.agrilink.usermanagementservice.dto.response.UserSummaryResponse;
import com.agrilink.usermanagementservice.enumeration.UserRole;
import com.agrilink.usermanagementservice.security.CurrentUser;
import com.agrilink.usermanagementservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    private final CurrentUser currentUser;

    public UserController(UserService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    // ---- Utilisateur authentifie ----

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createMyProfile(@Valid @RequestBody CreateProfileRequest request) {
        return service.createMyProfile(currentUser.keycloakId(), request);
    }

    @GetMapping("/me")
    public UserResponse getMyProfile() {
        return service.getMyProfile(currentUser.keycloakId());
    }

    @PutMapping("/me")
    public UserResponse updateMyProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return service.updateMyProfile(currentUser.keycloakId(), request);
    }

    // ---- Appels des autres microservices : donnees minimales ----

    @GetMapping("/keycloak/{keycloakId}")
    public UserSummaryResponse getSummary(@PathVariable String keycloakId) {
        return service.getSummary(keycloakId);
    }

    // ---- Administration ----

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<UserResponse> list(@RequestParam(required = false) String name,
                                           @RequestParam(required = false) UserRole role,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        return PageResponse.from(service.search(name, role,
                PageRequest.of(safePage, safeSize, Sort.by("lastName").ascending())));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public UserStatisticsResponse statistics() {
        return service.statistics();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }
}