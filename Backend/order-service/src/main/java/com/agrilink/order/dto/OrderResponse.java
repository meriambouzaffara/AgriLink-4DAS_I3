package com.agrilink.order.dto;

import com.agrilink.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long clientId,
        LocalDateTime dateCommande,
        BigDecimal montantTotal,
        OrderStatus statut,
        List<OrderItemResponse> lignes
) {
}
