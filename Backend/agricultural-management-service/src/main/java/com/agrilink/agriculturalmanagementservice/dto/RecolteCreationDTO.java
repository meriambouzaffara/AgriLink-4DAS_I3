package com.agrilink.agriculturalmanagementservice.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

public record RecolteCreationDTO(

        @NotNull(message = "Le produit est obligatoire")
        UUID produitId,

        @NotNull(message = "L'identifiant de l'agriculteur est obligatoire")
        UUID agriculteurId,

        @NotNull(message = "La quantité totale est obligatoire")
        @DecimalMin(value = "0.01",
                message = "La quantité doit être supérieure à zéro")
        Double quantiteTotale,

        @NotNull(message = "La date de récolte est obligatoire")
        @PastOrPresent(message = "La date de récolte ne peut pas être dans le futur")
        LocalDate dateRecolte,

        @NotNull(message = "La date de disponibilité est obligatoire")
        LocalDate dateDisponibilite,

        @Size(max = 500,
                message = "La description ne doit pas dépasser 500 caractères")
        String description

) {

    @AssertTrue(message =
            "La date de disponibilité doit être postérieure ou égale à la date de récolte")
    public boolean isDateDisponibiliteValide() {

        return dateRecolte == null
                || dateDisponibilite == null
                || !dateDisponibilite.isBefore(dateRecolte);
    }
}