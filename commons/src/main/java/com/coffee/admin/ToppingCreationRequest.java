package com.coffee.admin;

import java.math.BigDecimal;
import java.util.UUID;

public record ToppingCreationRequest(
        UUID uid,
        String name,
        BigDecimal price
) {
}
