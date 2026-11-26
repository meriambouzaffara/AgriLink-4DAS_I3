package com.agrilink.order.client;

import com.agrilink.order.exception.BusinessException;
import com.agrilink.order.exception.ResourceNotFoundException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Fictional local fixtures. Stock and reservation records reset on every restart. */
@Component
@Profile("dev-mock")
public class MockProductClient implements ProductClient {
    private final Map<Long, ProductInfo> catalog = new LinkedHashMap<>();
    private final Map<Long, BigDecimal> stock = new HashMap<>();
    private final Map<Long, Map<Long, BigDecimal>> reservations = new HashMap<>();

    public MockProductClient() {
        add(101L, "Tomates", "5.000", "100.000");
        add(102L, "Pommes de terre", "3.000", "150.000");
        add(103L, "Carottes", "2.500", "80.000");
        add(104L, "Oignons", "4.000", "120.000");
        add(105L, "Pommes", "6.000", "60.000");
    }

    private void add(Long id, String name, String price, String quantity) {
        var info = new ProductInfo(id, name, new BigDecimal(price), new BigDecimal(quantity));
        catalog.put(id, info);
        stock.put(id, info.availableQuantity());
    }

    public synchronized List<ProductInfo> listProducts() {
        return catalog.keySet().stream().map(this::getProduct).toList();
    }

    @Override
    public synchronized ProductInfo getProduct(Long productId) {
        var product = catalog.get(productId);
        if (product == null) throw new ResourceNotFoundException("Product " + productId + " not found");
        return new ProductInfo(product.id(), product.name(), product.unitPrice(), stock.get(productId));
    }

    @Override
    public synchronized boolean isAvailable(Long productId, BigDecimal quantity) {
        getProduct(productId);
        if (quantity == null || quantity.signum() <= 0) throw new IllegalArgumentException("Quantity must be positive");
        return stock.get(productId).compareTo(quantity) >= 0;
    }

    @Override
    public synchronized void reserveStock(Long orderId, List<StockItem> items) {
        if (orderId == null || orderId <= 0) throw new IllegalArgumentException("Order ID must be positive");
        if (reservations.containsKey(orderId)) throw new BusinessException("Order already has a stock reservation");
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("Reservation must contain items");
        Map<Long, BigDecimal> totals = new LinkedHashMap<>();
        for (var item : items) {
            if (item == null || item.quantity() == null || item.quantity().signum() <= 0)
                throw new IllegalArgumentException("Reservation quantity must be positive");
            getProduct(item.productId());
            totals.merge(item.productId(), item.quantity(), BigDecimal::add);
        }
        for (var entry : totals.entrySet()) {
            if (stock.get(entry.getKey()).compareTo(entry.getValue()) < 0)
                throw new BusinessException("Insufficient stock for product " + entry.getKey());
        }
        totals.forEach((id, quantity) -> stock.put(id, stock.get(id).subtract(quantity)));
        reservations.put(orderId, Map.copyOf(totals));
    }

    @Override
    public synchronized boolean releaseStock(Long orderId) {
        var reserved = reservations.remove(orderId);
        if (reserved == null) return false;
        reserved.forEach((id, quantity) -> stock.put(id, stock.get(id).add(quantity)));
        return true;
    }
}
