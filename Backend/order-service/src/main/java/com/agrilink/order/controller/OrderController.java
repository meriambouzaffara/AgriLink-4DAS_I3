package com.agrilink.order.controller;

import com.agrilink.order.dto.OrderRequest;
import com.agrilink.order.dto.OrderResponse;
import com.agrilink.order.exception.ResourceNotFoundException;
import com.agrilink.order.identity.CustomerIdentityResolver;
import com.agrilink.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orders;
    private final CustomerIdentityResolver identity;

    public OrderController(OrderService orders, CustomerIdentityResolver identity) {
        this.orders = orders;
        this.identity = identity;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request, HttpServletRequest httpRequest) {
        OrderResponse response = orders.createOrder(identity.currentCustomerId(httpRequest), request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri()).body(response);
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable Long id, HttpServletRequest request) {
        return orders.getOrder(id, identity.currentCustomerId(request));
    }

    @GetMapping("/customer/{id}")
    public List<OrderResponse> history(@PathVariable Long id, HttpServletRequest request) {
        Long customerId = identity.currentCustomerId(request);
        if (!customerId.equals(id)) throw new ResourceNotFoundException("Customer order history not found");
        return orders.getCustomerOrders(customerId);
    }

    @PatchMapping("/{id}/confirm")
    public OrderResponse confirm(@PathVariable Long id, HttpServletRequest request) {
        return orders.confirmOrder(id, identity.currentCustomerId(request));
    }

    @PatchMapping("/{id}/cancel")
    public OrderResponse cancel(@PathVariable Long id, HttpServletRequest request) {
        return orders.cancelOrder(id, identity.currentCustomerId(request));
    }
}
