package com.coffee.admin;

import java.math.BigDecimal;

public record ToppingRequest(
        String name,
        BigDecimal price
) {
}
