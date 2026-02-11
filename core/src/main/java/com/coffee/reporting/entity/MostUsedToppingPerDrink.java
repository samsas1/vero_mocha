package com.coffee.reporting.entity;

import java.util.UUID;

public record MostUsedToppingPerDrink(
        UUID productUid,
        String productName,
        UUID toppingUid,
        String toppingName,
        int totalToppingQuantity) {
}
