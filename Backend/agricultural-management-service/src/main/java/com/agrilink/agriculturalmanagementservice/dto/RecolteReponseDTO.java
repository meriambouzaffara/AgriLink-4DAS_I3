package com.agrilink.agriculturalmanagementservice.dto;

import com.agrilink.agriculturalmanagementservice.enums.StatutRecolte;

import java.time.LocalDate;
import java.util.UUID;

public record RecolteReponseDTO(
        UUID id,
        UUID produitId,
        UUID agriculteurId,
        Double quantiteTotale,
        Double quantiteDisponible,
        LocalDate dateRecolte,
        LocalDate dateDisponibilite,
        String description,
        StatutRecolte statut
) {}