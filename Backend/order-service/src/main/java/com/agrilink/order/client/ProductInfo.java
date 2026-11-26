package com.agrilink.order.client;

import java.math.BigDecimal;

/** Internal MS3 view of a product; this is not an MS2 wire contract. */
public record ProductInfo(Long id, String name, BigDecimal unitPrice, BigDecimal availableQuantity) {
}
