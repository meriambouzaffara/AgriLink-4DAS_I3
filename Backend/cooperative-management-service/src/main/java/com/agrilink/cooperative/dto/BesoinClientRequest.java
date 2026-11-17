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
public class BesoinClientRequest {

    @NotNull(message = "L'identifiant du client est obligatoire")
    private Long clientId;

    @NotNull(message = "L'identifiant du produit est obligatoire")
    private Long productId;

    @NotNull(message = "La quantite demandee est obligatoire")
    @DecimalMin(value = "0.01", message = "La quantite demandee doit etre superieure a 0")
    private BigDecimal quantiteDemandee;

    @NotNull(message = "La date souhaitee est obligatoire")
    @FutureOrPresent(message = "La date souhaitee ne peut pas etre passee")
    private LocalDate dateSouhaitee;

    private String description;
}
