package com.agrilink.agriculturalmanagementservice.dto;

import com.agrilink.agriculturalmanagementservice.enums.Categorie;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProduitReponseDTO(
        UUID id,
        String designation,
        String description,
        Categorie categorie,
        UUID creeParAgriculteurId,
        LocalDateTime dateCreation
) {}