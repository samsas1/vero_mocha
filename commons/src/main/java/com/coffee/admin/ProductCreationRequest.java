package com.coffee.admin;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductCreationRequest(
        UUID uid,
        String name,
        BigDecimal price
) {
}
