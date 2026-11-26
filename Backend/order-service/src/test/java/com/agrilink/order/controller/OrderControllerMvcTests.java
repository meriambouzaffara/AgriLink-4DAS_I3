package com.agrilink.order.controller;

import com.agrilink.order.client.MockProductClient;
import com.agrilink.order.config.DevCorsConfig;
import com.agrilink.order.dto.OrderItemResponse;
import com.agrilink.order.dto.OrderResponse;
import com.agrilink.order.entity.OrderStatus;
import com.agrilink.order.exception.BusinessException;
import com.agrilink.order.exception.GlobalExceptionHandler;
import com.agrilink.order.exception.ResourceNotFoundException;
import com.agrilink.order.identity.DevHeaderCustomerIdentityResolver;
import com.agrilink.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {OrderController.class, DevProductController.class, DevDeliveryController.class})
@ActiveProfiles("dev-mock")
@Import({DevHeaderCustomerIdentityResolver.class, DevCorsConfig.class, MockProductClient.class, GlobalExceptionHandler.class})
class OrderControllerMvcTests {
    @Autowired MockMvc mvc;
    @MockitoBean OrderService orders;

    private static final String VALID = "{\"lignes\":[{\"produitId\":101,\"quantite\":10}]}";

    private OrderResponse response(OrderStatus status) {
        return new OrderResponse(42L, 12L, LocalDateTime.of(2026, 11, 26, 12, 0),
                new BigDecimal("50.000"), status, List.of(new OrderItemResponse(3L, 101L,
                new BigDecimal("10.000"), new BigDecimal("5.000"), new BigDecimal("50.000"))));
    }

    @Test
    void createsOrderWithLocationAndSerializesDto() throws Exception {
        when(orders.createOrder(eq(12L), any())).thenReturn(response(OrderStatus.PENDING));
        mvc.perform(post("/api/orders").header("X-Customer-Id", "12").contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isCreated()).andExpect(header().string("Location", "http://localhost/api/orders/42"))
                .andExpect(jsonPath("$.id").value(42)).andExpect(jsonPath("$.clientId").value(12))
                .andExpect(jsonPath("$.montantTotal").value(50.000))
                .andExpect(jsonPath("$.lignes[0].prixUnitaire").value(5.000))
                .andExpect(jsonPath("$.lignes[0].commande").doesNotExist());
    }

    @Test
    void rejectsInvalidAndMalformedBodies() throws Exception {
        mvc.perform(post("/api/orders").header("X-Customer-Id", "12").contentType(MediaType.APPLICATION_JSON)
                .content("{\"lignes\":[]}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors.lignes").exists())
                .andExpect(jsonPath("$.timestamp").exists()).andExpect(jsonPath("$.path").value("/api/orders"));
        mvc.perform(post("/api/orders").header("X-Customer-Id", "12").contentType(MediaType.APPLICATION_JSON)
                .content("{broken"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Malformed JSON request"));
        verifyNoInteractions(orders);
    }

    @Test
    void rejectsMissingMalformedAndNonPositiveIdentity() throws Exception {
        mvc.perform(get("/api/orders/42")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
        for (String header : List.of("abc", "0", "-2"))
            mvc.perform(get("/api/orders/42").header("X-Customer-Id", header))
                    .andExpect(status().isBadRequest());
        verifyNoInteractions(orders);
    }

    @Test
    void retrievesOwnedOrderAndHandlesMissingAndInvalidId() throws Exception {
        when(orders.getOrder(42L, 12L)).thenReturn(response(OrderStatus.PENDING));
        mvc.perform(get("/api/orders/42").header("X-Customer-Id", "12"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.statut").value("PENDING"));
        when(orders.getOrder(99L, 12L)).thenThrow(new ResourceNotFoundException("Order 99 not found"));
        mvc.perform(get("/api/orders/99").header("X-Customer-Id", "12"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.error").value("Not Found"));
        mvc.perform(get("/api/orders/nope").header("X-Customer-Id", "12"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Invalid path parameter: id"));
    }

    @Test
    void protectsOwnershipAndHistory() throws Exception {
        when(orders.getOrder(42L, 13L)).thenThrow(new ResourceNotFoundException("Order 42 not found"));
        mvc.perform(get("/api/orders/42").header("X-Customer-Id", "13"))
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/orders/customer/12").header("X-Customer-Id", "13"))
                .andExpect(status().isNotFound());
        verify(orders, never()).getCustomerOrders(12L);
        when(orders.getCustomerOrders(12L)).thenReturn(List.of(response(OrderStatus.PENDING)));
        mvc.perform(get("/api/orders/customer/12").header("X-Customer-Id", "12"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(42));
        when(orders.getCustomerOrders(14L)).thenReturn(List.of());
        mvc.perform(get("/api/orders/customer/14").header("X-Customer-Id", "14"))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test
    void confirmsCancelsAndMapsBusinessConflicts() throws Exception {
        when(orders.confirmOrder(42L, 12L)).thenReturn(response(OrderStatus.CONFIRMED));
        when(orders.cancelOrder(42L, 12L)).thenReturn(response(OrderStatus.CANCELLED));
        mvc.perform(patch("/api/orders/42/confirm").header("X-Customer-Id", "12"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.statut").value("CONFIRMED"));
        mvc.perform(patch("/api/orders/42/cancel").header("X-Customer-Id", "12"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.statut").value("CANCELLED"));
        when(orders.confirmOrder(43L, 12L)).thenThrow(new BusinessException("Insufficient stock"));
        mvc.perform(patch("/api/orders/43/confirm").header("X-Customer-Id", "12"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.message").value("Insufficient stock"));
        when(orders.cancelOrder(43L, 12L)).thenThrow(new BusinessException("Order can no longer be cancelled"));
        mvc.perform(patch("/api/orders/43/cancel").header("X-Customer-Id", "12"))
                .andExpect(status().isConflict());
    }

    @Test
    void listsCurrentDevelopmentProducts() throws Exception {
        mvc.perform(get("/api/dev/products")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].id").value(101))
                .andExpect(jsonPath("$[0].unitPrice").value(5.000))
                .andExpect(jsonPath("$[0].availableQuantity").value(100.000));
    }

    @Test
    void simulatesDeliveryOnlyThroughService() throws Exception {
        when(orders.updateDeliveryStatus(42L, OrderStatus.IN_PREPARATION)).thenReturn(response(OrderStatus.IN_PREPARATION));
        mvc.perform(patch("/api/dev/orders/42/status").contentType(MediaType.APPLICATION_JSON)
                .content("{\"statut\":\"IN_PREPARATION\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.statut").value("IN_PREPARATION"));
        mvc.perform(patch("/api/dev/orders/42/status").contentType(MediaType.APPLICATION_JSON)
                .content("{\"statut\":null}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors.statut").exists());
        when(orders.updateDeliveryStatus(42L, OrderStatus.DELIVERED)).thenThrow(new BusinessException("Invalid order status transition"));
        mvc.perform(patch("/api/dev/orders/42/status").contentType(MediaType.APPLICATION_JSON)
                .content("{\"statut\":\"DELIVERED\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void allowsConfiguredDevelopmentCorsOnly() throws Exception {
        mvc.perform(options("/api/orders").header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Content-Type,X-Customer-Id"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
        mvc.perform(options("/api/orders").header("Origin", "http://untrusted.example")
                .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }
}
