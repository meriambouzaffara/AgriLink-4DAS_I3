package com.agrilink.cooperative.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represente le besoin d'un client en un produit agricole, avec la
 * quantite demandee et la date souhaitee.
 */
@Entity
@Table(name = "besoins_clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BesoinClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantiteDemandee;

    @Column(nullable = false)
    private LocalDate dateSouhaitee;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private StatutBesoin statut = StatutBesoin.EN_ATTENTE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @OneToOne(mappedBy = "besoin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private AnnonceCollecte annonceCollecte;

    @PrePersist
    protected void onCreate() {
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
        if (this.statut == null) {
            this.statut = StatutBesoin.EN_ATTENTE;
        }
    }

    public boolean estCouvertParQuantiteRecue(BigDecimal quantiteRecueTotale) {
        if (quantiteRecueTotale == null || this.quantiteDemandee == null) {
            return false;
        }
        return quantiteRecueTotale.compareTo(this.quantiteDemandee) >= 0;
    }
}
