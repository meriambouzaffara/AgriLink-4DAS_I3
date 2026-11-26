package com.agrilink.order.client;

import java.math.BigDecimal;

public record StockItem(Long productId, BigDecimal quantity) {
}
