package com.agrilink.agriculturalmanagementservice.dto;

import com.agrilink.agriculturalmanagementservice.enums.StatutOffre;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record OffreReponseDTO(
        UUID id,
        UUID produitId,
        UUID agriculteurId,
        Double prix,
        Double quantite,
        LocalDate dateDisponibilite,
        StatutOffre statut,
        LocalDateTime datePublication
) {}
