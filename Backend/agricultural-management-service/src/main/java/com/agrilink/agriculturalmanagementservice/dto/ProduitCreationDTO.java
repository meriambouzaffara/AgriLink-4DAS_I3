package com.agrilink.agriculturalmanagementservice.dto;

import com.agrilink.agriculturalmanagementservice.enums.Categorie;
import jakarta.validation.constraints.*;

import java.util.UUID;

public record ProduitCreationDTO(

        @NotBlank(message = "La désignation est obligatoire")
        @Size(max = 120,
                message = "La désignation ne doit pas dépasser 120 caractères")
        String designation,

        @NotBlank(message = "La description est obligatoire")
        @Size(max = 500,
                message = "La description ne doit pas dépasser 500 caractères")
        String description,

        @NotNull(message = "La catégorie est obligatoire")
        Categorie categorie,

        @NotNull(message = "L'identifiant de l'agriculteur est obligatoire")
        UUID creeParAgriculteurId

) {}