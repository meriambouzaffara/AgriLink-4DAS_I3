package com.agrilink.order.client;

import java.math.BigDecimal;
import java.util.List;

/** Internal abstraction only. The future MS2 protocol still requires agreement. */
public interface ProductClient {
    ProductInfo getProduct(Long productId);

    default BigDecimal getUnitPrice(Long productId) {
        return getProduct(productId).unitPrice();
    }

    boolean isAvailable(Long productId, BigDecimal quantity);

    /** Atomically reserves every item, or throws without reserving any. */
    void reserveStock(Long orderId, List<StockItem> items);

    /** Returns false if this process has no matching live reservation. */
    boolean releaseStock(Long orderId);
}
