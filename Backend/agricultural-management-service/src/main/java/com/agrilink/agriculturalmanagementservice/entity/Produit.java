package com.agrilink.agriculturalmanagementservice.entity;

import com.agrilink.agriculturalmanagementservice.enums.Categorie;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "produits")
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 120)
    private String designation;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Categorie categorie;

    // Identifiant externe de l'agriculteur créateur (MS1)
    @Column(name = "cree_par_agriculteur_id", nullable = false)
    private UUID creeParAgriculteurId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    // Stockage de l'image sur Cloudinary
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "image_public_id", length = 200)
    private String imagePublicId;

    @PrePersist
    protected void initialiserDateCreation() {
        this.dateCreation = LocalDateTime.now();
    }
}
