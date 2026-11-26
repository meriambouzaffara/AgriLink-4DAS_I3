package com.agrilink.usermanagementservice.entity;

import com.agrilink.usermanagementservice.enumeration.UserRole;
import com.agrilink.usermanagementservice.enumeration.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "users")   // "user" est un mot reserve en SQL
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identifiant du compte Keycloak (claim "sub" du JWT)
    @Column(nullable = false, unique = true, updatable = false)
    private String keycloakId;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String address;

    // Categorie metier, fixee a la creation
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Constructeur sans argument : requis par JPA
    protected User() {
    }

    public User(String keycloakId, String firstName, String lastName,
                String phone, String address, UserRole userRole) {
        this.keycloakId = keycloakId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.userRole = userRole;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePersonalInfo(String firstName, String lastName,
                                   String phone, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }

}