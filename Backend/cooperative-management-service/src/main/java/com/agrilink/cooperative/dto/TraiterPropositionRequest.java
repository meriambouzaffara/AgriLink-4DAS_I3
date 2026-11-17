package com.agrilink.cooperative.dto;

import com.agrilink.cooperative.entity.StatutProposition;
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
public class TraiterPropositionRequest {

    @NotNull(message = "Le statut est obligatoire")
    private StatutProposition statut;

    @DecimalMin(value = "0.00", message = "La quantite acceptee ne peut pas etre negative")
    private BigDecimal quantiteAcceptee;
}
