package com.coffee.reporting.entity;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductToppingUsageCounts(
        UUID productUid,
        String productName,
        UUID toppingUid,
        String toppingName,
        Integer totalProductOrderQuantity,
        BigDecimal totalToppingOrderQuantityPerProduct) {
}
