package com.agrilink.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long produitId,
        BigDecimal quantite,
        BigDecimal prixUnitaire,
        BigDecimal sousTotal
) {
}
