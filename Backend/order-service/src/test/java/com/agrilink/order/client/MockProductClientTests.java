package com.agrilink.order.client;

import com.agrilink.order.exception.BusinessException;
import com.agrilink.order.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class MockProductClientTests {
    private final MockProductClient client = new MockProductClient();

    private StockItem item(long id, String quantity) {
        return new StockItem(id, new BigDecimal(quantity));
    }

    @Test
    void reservesAllItemsAndAggregatesDuplicates() {
        client.reserveStock(1L, List.of(item(101, "10"), item(101, "5"), item(102, "20")));
        assertEquals(new BigDecimal("85.000"), client.getProduct(101L).availableQuantity());
        assertEquals(new BigDecimal("130.000"), client.getProduct(102L).availableQuantity());
    }

    @Test
    void insufficientStockDoesNotPartiallyReserve() {
        assertThrows(BusinessException.class, () -> client.reserveStock(1L,
                List.of(item(101, "10"), item(102, "151"))));
        assertEquals(new BigDecimal("100.000"), client.getProduct(101L).availableQuantity());
        assertFalse(client.releaseStock(1L));
    }

    @Test
    void duplicateReservationAndReleaseAreSafe() {
        client.reserveStock(1L, List.of(item(101, "10")));
        assertThrows(BusinessException.class, () -> client.reserveStock(1L, List.of(item(101, "10"))));
        assertTrue(client.releaseStock(1L));
        assertFalse(client.releaseStock(1L));
        assertEquals(new BigDecimal("100.000"), client.getProduct(101L).availableQuantity());
    }

    @Test
    void rejectsUnknownProductAndNeverMakesStockNegative() {
        assertThrows(ResourceNotFoundException.class, () -> client.getProduct(999L));
        assertThrows(ResourceNotFoundException.class, () -> client.reserveStock(1L, List.of(item(999, "1"))));
        assertThrows(BusinessException.class, () -> client.reserveStock(2L, List.of(item(105, "61"))));
        assertEquals(new BigDecimal("60.000"), client.getProduct(105L).availableQuantity());
    }

    @Test
    void newClientHasFreshDeterministicStockAndNoOldReservation() {
        client.reserveStock(1L, List.of(item(101, "10")));
        var restarted = new MockProductClient();
        assertEquals(new BigDecimal("100.000"), restarted.getProduct(101L).availableQuantity());
        assertFalse(restarted.releaseStock(1L));
    }

    @Test
    void concurrentReservationsCannotOversell() throws Exception {
        var ready = new CountDownLatch(2);
        var start = new CountDownLatch(1);
        var successes = new AtomicInteger();
        var pool = Executors.newFixedThreadPool(2);
        try {
            var tasks = List.of(1L, 2L).stream().map(id -> pool.submit(() -> {
                ready.countDown();
                start.await();
                try {
                    client.reserveStock(id, List.of(item(101, "60")));
                    successes.incrementAndGet();
                } catch (BusinessException expected) {
                    // Only one reservation can consume the available 100 kg.
                }
                return null;
            })).toList();
            assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();
            for (var task : tasks) task.get(5, TimeUnit.SECONDS);
        } finally {
            pool.shutdownNow();
        }
        assertEquals(1, successes.get());
        assertEquals(new BigDecimal("40.000"), client.getProduct(101L).availableQuantity());
    }
}
