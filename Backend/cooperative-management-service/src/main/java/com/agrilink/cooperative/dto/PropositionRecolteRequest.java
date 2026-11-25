package com.agrilink.cooperative.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropositionRecolteRequest {

    private Long annonceId;

    @NotNull(message = "L'identifiant de l'agriculteur est obligatoire")
    private Long agriculteurId;

    @NotNull(message = "L'identifiant de la recolte est obligatoire")
    private Long recolteId;

    @NotNull(message = "La quantite proposee est obligatoire")
    @DecimalMin(value = "0.01", message = "La quantite proposee doit etre superieure a 0")
    private BigDecimal quantiteProposee;

    private String commentaire;
}
