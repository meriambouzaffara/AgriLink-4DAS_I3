package com.agrilink.cooperative.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represente une annonce publiee pour collecter la quantite demandee
 * aupres des agriculteurs.
 */
@Entity
@Table(name = "annonces_collectes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnonceCollecte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "besoin_id", unique = true)
    private BesoinClient besoin;

    @Column(name = "responsable_id", nullable = false)
    private Long responsableId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantiteObjectif;

    @Column(precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal quantiteAcceptee = BigDecimal.ZERO;

    private LocalDateTime datePublication;

    private LocalDate dateLimite;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private StatutAnnonce statut = StatutAnnonce.OUVERTE;

    @OneToMany(mappedBy = "annonce", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PropositionRecolte> propositions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.datePublication == null) {
            this.datePublication = LocalDateTime.now();
        }
        if (this.quantiteAcceptee == null) {
            this.quantiteAcceptee = BigDecimal.ZERO;
        }
        if (this.statut == null) {
            this.statut = StatutAnnonce.OUVERTE;
        }
    }

    public Long getBesoinId() {
        return this.besoin != null ? this.besoin.getId() : null;
    }
}
