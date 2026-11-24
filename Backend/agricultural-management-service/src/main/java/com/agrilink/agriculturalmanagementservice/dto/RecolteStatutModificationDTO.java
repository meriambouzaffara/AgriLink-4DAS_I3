package com.agrilink.agriculturalmanagementservice.dto;

import com.agrilink.agriculturalmanagementservice.enums.StatutRecolte;
import jakarta.validation.constraints.NotNull;

public record RecolteStatutModificationDTO(
        @NotNull(message = "Le statut de la récolte est obligatoire")
        StatutRecolte statut
) {}
