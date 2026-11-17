package com.agrilink.cooperative.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnonceCollecteRequest {

    @NotNull(message = "L'identifiant du besoin client est obligatoire")
    private Long besoinId;

    @NotNull(message = "L'identifiant du responsable de cooperative est obligatoire")
    private Long responsableId;

    @NotNull(message = "La quantite objectif est obligatoire")
    @DecimalMin(value = "0.01", message = "La quantite objectif doit etre superieure a 0")
    private BigDecimal quantiteObjectif;

    @NotNull(message = "La date limite est obligatoire")
    @FutureOrPresent(message = "La date limite ne peut pas etre passee")
    private LocalDate dateLimite;

    private String description;
}
