package com.agrilink.order.service;

import com.agrilink.order.client.MockProductClient;
import com.agrilink.order.client.ProductClient;
import com.agrilink.order.client.ProductInfo;
import com.agrilink.order.dto.OrderItemRequest;
import com.agrilink.order.dto.OrderRequest;
import com.agrilink.order.entity.Order;
import com.agrilink.order.entity.OrderItem;
import com.agrilink.order.entity.OrderStatus;
import com.agrilink.order.exception.BusinessException;
import com.agrilink.order.exception.InvalidOrderException;
import com.agrilink.order.exception.ResourceNotFoundException;
import com.agrilink.order.mapper.OrderMapper;
import com.agrilink.order.repository.OrderRepository;
import com.agrilink.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceImplTests {
    private OrderRepository repository;
    private MockProductClient products;
    private OrderServiceImpl service;
    private Order saved;

    @BeforeEach
    void setup() {
        repository = mock(OrderRepository.class);
        products = new MockProductClient();
        service = new OrderServiceImpl(repository, products, new OrderMapper());
        when(repository.saveAndFlush(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            if (order.getId() == null) order.setId(1L);
            saved = order;
            return order;
        });
        when(repository.findByIdForUpdate(1L)).thenAnswer(invocation -> Optional.ofNullable(saved));
        when(repository.findWithLignesById(1L)).thenAnswer(invocation -> Optional.ofNullable(saved));
    }

    private OrderRequest request(Object... pairs) {
        var lines = new java.util.ArrayList<OrderItemRequest>();
        for (int i = 0; i < pairs.length; i += 2)
            lines.add(new OrderItemRequest((Long) pairs[i], new BigDecimal((String) pairs[i + 1])));
        return new OrderRequest(lines);
    }

    @Test
    void createsOneLineWithCustomerAndParentRelationship() {
        var response = service.createOrder(7L, request(101L, "2"));
        assertEquals(7L, response.clientId());
        assertEquals(OrderStatus.PENDING, response.statut());
        assertNotNull(response.dateCommande());
        assertEquals(new BigDecimal("10.000"), response.montantTotal());
        assertEquals(new BigDecimal("5.000"), response.lignes().get(0).prixUnitaire());
        assertSame(saved, saved.getLignes().get(0).getCommande());
        assertEquals(new BigDecimal("100.000"), products.getProduct(101L).availableQuantity());
    }

    @Test
    void createsMultipleLinesAndRoundsEachSubtotalHalfUp() {
        var response = service.createOrder(7L, request(103L, "0.001", 101L, "2.000"));
        assertEquals(new BigDecimal("0.003"), response.lignes().get(0).sousTotal());
        assertEquals(new BigDecimal("10.003"), response.montantTotal());
        assertEquals(2, response.lignes().size());
    }

    @Test
    void duplicateQuantitiesAreCheckedTogether() {
        assertThrows(BusinessException.class, () -> service.createOrder(7L,
                request(101L, "60", 101L, "50")));
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void rejectsUnknownEmptyAndInvalidInput() {
        assertThrows(ResourceNotFoundException.class, () -> service.createOrder(7L, request(999L, "1")));
        assertThrows(InvalidOrderException.class, () -> service.createOrder(7L, request()));
        assertThrows(InvalidOrderException.class, () -> service.createOrder(7L, request(101L, "0")));
        assertThrows(InvalidOrderException.class, () -> service.createOrder(7L, request(101L, "1.0001")));
        assertThrows(InvalidOrderException.class, () -> service.createOrder(7L, request(101L, "1000000000000")));
        assertThrows(InvalidOrderException.class, () -> service.createOrder(0L, request(101L, "1")));
    }

    @Test
    void rejectsMonetaryOverflowBeforePersistence() {
        ProductClient expensive = mock(ProductClient.class);
        when(expensive.getProduct(101L)).thenReturn(new ProductInfo(101L, "Test", new BigDecimal("999999999999.999"), new BigDecimal("10")));
        when(expensive.isAvailable(101L, new BigDecimal("2"))).thenReturn(true);
        var pricedService = new OrderServiceImpl(repository, expensive, new OrderMapper());
        assertThrows(InvalidOrderException.class, () -> pricedService.createOrder(7L, request(101L, "2")));
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void confirmsOnceAndPreventsConfirmationAfterCancellation() {
        service.createOrder(7L, request(101L, "10"));
        assertEquals(OrderStatus.CONFIRMED, service.confirmOrder(1L, 7L).statut());
        assertEquals(OrderStatus.CONFIRMED, service.confirmOrder(1L, 7L).statut());
        assertEquals(new BigDecimal("90.000"), products.getProduct(101L).availableQuantity());
        service.cancelOrder(1L, 7L);
        assertThrows(BusinessException.class, () -> service.confirmOrder(1L, 7L));
    }

    @Test
    void insufficientStockAtConfirmationPreservesPending() {
        service.createOrder(7L, request(101L, "10"));
        products.reserveStock(99L, List.of(new com.agrilink.order.client.StockItem(101L, new BigDecimal("95"))));
        assertThrows(BusinessException.class, () -> service.confirmOrder(1L, 7L));
        assertEquals(OrderStatus.PENDING, saved.getStatut());
    }

    @Test
    void pendingCancellationDoesNotReleaseAndConfirmedCancellationRestoresOnce() {
        service.createOrder(7L, request(101L, "10"));
        assertEquals(OrderStatus.CANCELLED, service.cancelOrder(1L, 7L).statut());
        assertEquals(new BigDecimal("100.000"), products.getProduct(101L).availableQuantity());
        service.createOrder(7L, request(101L, "10"));
        service.confirmOrder(1L, 7L);
        service.cancelOrder(1L, 7L);
        service.cancelOrder(1L, 7L);
        assertEquals(new BigDecimal("100.000"), products.getProduct(101L).availableQuantity());
    }

    @Test
    void missingReservationCannotInflateStock() {
        service.createOrder(7L, request(101L, "10"));
        service.confirmOrder(1L, 7L);
        var restarted = new MockProductClient();
        var newService = new OrderServiceImpl(repository, restarted, new OrderMapper());
        assertThrows(BusinessException.class, () -> newService.cancelOrder(1L, 7L));
        assertEquals(OrderStatus.CONFIRMED, saved.getStatut());
        assertEquals(new BigDecimal("100.000"), restarted.getProduct(101L).availableQuantity());
    }

    @Test
    void saveFailureAfterConfirmationReleasesStock() {
        service.createOrder(7L, request(101L, "10"));
        when(repository.saveAndFlush(saved)).thenThrow(new IllegalStateException("database failure"));
        assertThrows(IllegalStateException.class, () -> service.confirmOrder(1L, 7L));
        assertEquals(OrderStatus.PENDING, saved.getStatut());
        assertEquals(new BigDecimal("100.000"), products.getProduct(101L).availableQuantity());
    }

    @Test
    void saveFailureAfterCancellationRestoresReservation() {
        service.createOrder(7L, request(101L, "10"));
        service.confirmOrder(1L, 7L);
        when(repository.saveAndFlush(saved)).thenThrow(new IllegalStateException("database failure"));
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(1L, 7L));
        assertEquals(OrderStatus.CONFIRMED, saved.getStatut());
        assertEquals(new BigDecimal("90.000"), products.getProduct(101L).availableQuantity());
    }

    @Test
    void rejectsUnauthorizedCustomerAndMissingOrder() {
        service.createOrder(7L, request(101L, "1"));
        assertThrows(ResourceNotFoundException.class, () -> service.getOrder(1L, 8L));
        assertThrows(ResourceNotFoundException.class, () -> service.confirmOrder(1L, 8L));
        assertThrows(ResourceNotFoundException.class, () -> service.cancelOrder(1L, 8L));
        assertThrows(ResourceNotFoundException.class, () -> service.getOrder(2L, 7L));
    }

    @Test
    void deliveryTransitionsAreSequentialAndTerminal() {
        service.createOrder(7L, request(101L, "1"));
        assertThrows(BusinessException.class, () -> service.updateDeliveryStatus(1L, OrderStatus.IN_PREPARATION));
        service.confirmOrder(1L, 7L);
        assertThrows(BusinessException.class, () -> service.updateDeliveryStatus(1L, OrderStatus.SHIPPED));
        assertEquals(OrderStatus.IN_PREPARATION, service.updateDeliveryStatus(1L, OrderStatus.IN_PREPARATION).statut());
        assertEquals(OrderStatus.IN_PREPARATION, service.updateDeliveryStatus(1L, OrderStatus.IN_PREPARATION).statut());
        assertThrows(BusinessException.class, () -> service.cancelOrder(1L, 7L));
        assertThrows(BusinessException.class, () -> service.updateDeliveryStatus(1L, OrderStatus.CONFIRMED));
        service.updateDeliveryStatus(1L, OrderStatus.SHIPPED);
        service.updateDeliveryStatus(1L, OrderStatus.DELIVERED);
        assertThrows(BusinessException.class, () -> service.updateDeliveryStatus(1L, OrderStatus.CANCELLED));
        assertThrows(BusinessException.class, () -> service.cancelOrder(1L, 7L));
    }

    @Test
    void retrievesOrderAndEmptyHistory() {
        service.createOrder(7L, request(101L, "1"));
        assertEquals(1L, service.getOrder(1L, 7L).id());
        when(repository.findByClientIdOrderByDateCommandeDescIdDesc(7L)).thenReturn(List.of(saved));
        assertEquals(1, service.getCustomerOrders(7L).size());
        assertTrue(service.getCustomerOrders(8L).isEmpty());
    }
}
