package com.agrilink.agriculturalmanagementservice.entity;

import com.agrilink.agriculturalmanagementservice.enums.StatutRecolte;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "recoltes")
public class Recolte {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Relation avec Produit dans MS2
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    // Identifiant externe provenant de MS1
    @Column(name = "agriculteur_id", nullable = false)
    private UUID agriculteurId;

    @Column(name = "quantite_totale", nullable = false)
    private Double quantiteTotale;

    @Column(name = "quantite_disponible", nullable = false)
    private Double quantiteDisponible;

    @Column(name = "date_recolte", nullable = false)
    private LocalDate dateRecolte;

    @Column(name = "date_disponibilite", nullable = false)
    private LocalDate dateDisponibilite;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutRecolte statut;

    @PrePersist
    protected void initialiserRecolte() {

        if (this.quantiteDisponible == null) {
            this.quantiteDisponible = this.quantiteTotale;
        }

        if (this.statut == null) {
            this.statut = StatutRecolte.DISPONIBLE;
        }
    }
}