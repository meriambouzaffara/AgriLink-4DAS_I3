package com.agrilink.cooperative.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represente une quantite proposee par un agriculteur a partir
 * d'une recolte, avec les quantites acceptee et reellement recue.
 */
@Entity
@Table(name = "propositions_recoltes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropositionRecolte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annonce_id", nullable = false)
    private AnnonceCollecte annonce;

    @Column(nullable = false)
    private Long agriculteurId;

    @Column(nullable = false)
    private Long recolteId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantiteProposee;

    @Column(precision = 12, scale = 2)
    private BigDecimal quantiteAcceptee;

    @Column(precision = 12, scale = 2)
    private BigDecimal quantiteRecue;

    @Column(nullable = false)
    private LocalDateTime dateProposition;

    private LocalDateTime dateReception;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 35)
    @Builder.Default
    private StatutProposition statut = StatutProposition.EN_ATTENTE;

    @Column(length = 1000)
    private String commentaire;

    @PrePersist
    protected void onCreate() {
        if (this.dateProposition == null) {
            this.dateProposition = LocalDateTime.now();
        }
        if (this.statut == null) {
            this.statut = StatutProposition.EN_ATTENTE;
        }
    }

    public Long getAnnonceId() {
        return this.annonce != null ? this.annonce.getId() : null;
    }
}
