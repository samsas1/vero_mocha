package com.coffee.order.entity;

import java.math.BigDecimal;
import java.util.UUID;

public record CartTotalsEntity(
        UUID cartUid,
        UUID productItemUid,
        UUID toppingItemUid,
        UUID productUid,
        UUID toppingUid,
        int productItemQuantity,
        int toppingItemPerProductItemQuantity,
        BigDecimal productPrice,
        BigDecimal toppingPrice
) {
}
