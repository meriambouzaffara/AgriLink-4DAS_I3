package com.agrilink.cooperative.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entité représentant une demande d'adhésion soumise par un agriculteur
 * et en attente d'acceptation ou de refus par l'administrateur de la coopérative.
 */
@Entity
@Table(name = "membership_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cooperative_id", nullable = false)
    private Cooperative cooperative;

    /** Identifiant de l'agriculteur demandeur (référence vers MS1 user-management-service) */
    @Column(nullable = false)
    private Long farmerUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MembershipRequestStatus status = MembershipRequestStatus.PENDING;

    private String message;

    @Column(nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        this.requestedAt = LocalDateTime.now();
    }
}
