package com.agrilink.order.service;

import com.agrilink.order.dto.OrderRequest;
import com.agrilink.order.dto.OrderResponse;
import com.agrilink.order.entity.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(Long customerId, OrderRequest request);
    OrderResponse getOrder(Long orderId, Long customerId);
    List<OrderResponse> getCustomerOrders(Long customerId);
    OrderResponse confirmOrder(Long orderId, Long customerId);
    OrderResponse cancelOrder(Long orderId, Long customerId);

    /** Internal operation: caller must authorize an MS4 delivery update before invoking. */
    OrderResponse updateDeliveryStatus(Long orderId, OrderStatus newStatus);
}
