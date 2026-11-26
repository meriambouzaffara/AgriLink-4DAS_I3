package com.agrilink.order.mapper;

import com.agrilink.order.dto.OrderItemResponse;
import com.agrilink.order.dto.OrderResponse;
import com.agrilink.order.entity.Order;
import com.agrilink.order.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {
    public OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getClientId(),
                order.getDateCommande(),
                order.getMontantTotal(),
                order.getStatut(),
                order.getLignes().stream().map(this::toResponse).toList()
        );
    }

    public OrderItemResponse toResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduitId(),
                item.getQuantite(),
                item.getPrixUnitaire(),
                item.getSousTotal()
        );
    }

    public List<OrderResponse> toResponses(List<Order> orders) {
        return orders.stream().map(this::toResponse).toList();
    }
}
