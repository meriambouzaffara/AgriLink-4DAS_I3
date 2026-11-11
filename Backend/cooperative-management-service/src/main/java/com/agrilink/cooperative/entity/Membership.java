package com.agrilink.cooperative.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entité représentant l'adhésion active d'un agriculteur à une coopérative.
 */
@Entity
@Table(name = "memberships",
       uniqueConstraints = @UniqueConstraint(columnNames = {"cooperative_id", "farmer_user_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cooperative_id", nullable = false)
    private Cooperative cooperative;

    /** Identifiant de l'agriculteur membre (référence vers MS1 user-management-service) */
    @Column(nullable = false)
    private Long farmerUserId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate() {
        this.joinedAt = LocalDateTime.now();
    }
}
