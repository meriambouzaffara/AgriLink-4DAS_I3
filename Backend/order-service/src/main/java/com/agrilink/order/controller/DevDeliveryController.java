package com.agrilink.order.controller;

import com.agrilink.order.dto.DevStatusRequest;
import com.agrilink.order.dto.OrderResponse;
import com.agrilink.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("dev-mock")
@RequestMapping("/api/dev/orders")
public class DevDeliveryController {
    private final OrderService orders;

    public DevDeliveryController(OrderService orders) {
        this.orders = orders;
    }

    @PatchMapping("/{id}/status")
    public OrderResponse update(@PathVariable Long id, @Valid @RequestBody DevStatusRequest request) {
        return orders.updateDeliveryStatus(id, request.statut());
    }
}
