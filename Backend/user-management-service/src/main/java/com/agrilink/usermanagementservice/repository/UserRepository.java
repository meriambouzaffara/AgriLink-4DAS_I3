package com.agrilink.usermanagementservice.repository;

import com.agrilink.usermanagementservice.entity.User;
import com.agrilink.usermanagementservice.enumeration.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByKeycloakId(String keycloakId);

    boolean existsByKeycloakId(String keycloakId);

    long countByUserRole(UserRole userRole);
}