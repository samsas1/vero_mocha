package com.coffee.reporting.entity;

import java.util.UUID;

public record ToppingOrderCountPerProduct(
        UUID productUid,
        UUID toppingUid,
        String toppingName,
        Integer totalToppingOrderPerProductQuantity
) {
}
