package com.agrilink.agriculturalmanagementservice.entity;

import com.agrilink.agriculturalmanagementservice.enums.StatutOffre;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "offres")
public class Offre {

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

    @Column(nullable = false)
    private Double prix;

    @Column(nullable = false)
    private Double quantite;

    @Column(name = "date_disponibilite", nullable = false)
    private LocalDate dateDisponibilite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutOffre statut;

    @Column(name = "date_publication")
    private LocalDateTime datePublication;

    @PrePersist
    protected void initialiserStatut() {
        if (this.statut == null) {
            this.statut = StatutOffre.BROUILLON;
        }
    }

    public void publierOffre() {
        this.statut = StatutOffre.PUBLIEE;
        this.datePublication = LocalDateTime.now();
    }

    public void retirerOffre() {
        this.statut = StatutOffre.RETIREE;
    }

    public void archiverOffre() {
        this.statut = StatutOffre.ARCHIVEE;
    }
}