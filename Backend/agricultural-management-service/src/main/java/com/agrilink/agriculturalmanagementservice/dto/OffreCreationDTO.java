package com.agrilink.agriculturalmanagementservice.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

public record OffreCreationDTO(

        @NotNull(message = "Le produit est obligatoire")
        UUID produitId,

        @NotNull(message = "L'identifiant de l'agriculteur est obligatoire")
        UUID agriculteurId,

        @NotNull(message = "Le prix est obligatoire")
        @DecimalMin(value = "0.01",
                message = "Le prix doit être supérieur à zéro")
        Double prix,

        @NotNull(message = "La quantité est obligatoire")
        @DecimalMin(value = "0.01",
                message = "La quantité doit être supérieure à zéro")
        Double quantite,

        @NotNull(message = "La date de disponibilité est obligatoire")
        @FutureOrPresent(message =
                "La date de disponibilité doit être aujourd'hui ou dans le futur")
        LocalDate dateDisponibilite

) {}