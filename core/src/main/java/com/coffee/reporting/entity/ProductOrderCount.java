package com.coffee.reporting.entity;

import java.util.UUID;

public record ProductOrderCount(
        UUID productUid,
        String productName,
        Integer totalProductOrderQuantity
) {
}
