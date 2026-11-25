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
public class ReceptionRecolteRequest {

    @NotNull(message = "La quantite recue est obligatoire")
    @DecimalMin(value = "0.01", message = "La quantite recue doit etre superieure a 0")
    private BigDecimal quantiteRecue;
}
