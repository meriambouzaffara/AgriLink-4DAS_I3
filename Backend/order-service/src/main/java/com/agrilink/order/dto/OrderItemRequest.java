package com.agrilink.order.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderItemRequest(
        @NotNull @Positive Long produitId,
        @NotNull @Positive @Digits(integer = 12, fraction = 3) BigDecimal quantite
) {
}
