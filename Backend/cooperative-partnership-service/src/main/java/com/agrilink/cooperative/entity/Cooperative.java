package com.agrilink.cooperative.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une coopérative agricole.
 * Une coopérative regroupe des agriculteurs et centralise certaines informations ou offres.
 */
@Entity
@Table(name = "cooperatives")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cooperative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1000)
    private String description;

    /** Identifiant de l'administrateur (référence vers MS1 user-management-service) */
    @Column(nullable = false)
    private Long adminUserId;

    private String location;

    private String contactEmail;

    private String contactPhone;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "cooperative", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Membership> memberships = new ArrayList<>();

    @OneToMany(mappedBy = "cooperative", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MembershipRequest> membershipRequests = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
