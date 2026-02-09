package com.coffee.admin;

import java.math.BigDecimal;

public record ProductRequest(
        String name,
        BigDecimal price
) {
}
