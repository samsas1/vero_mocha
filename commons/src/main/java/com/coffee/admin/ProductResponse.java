package com.coffee.admin;

import com.coffee.enumerators.ItemStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID uid,
        String name,
        BigDecimal price,
        ItemStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
