package com.agrilink.order.dto;

import com.agrilink.order.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record DevStatusRequest(@NotNull OrderStatus statut) {
}
