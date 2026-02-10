package com.coffee.order.entity;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public record CartTotalsEntity(
        UUID cartUid,
        UUID productItemUid,
        Optional<UUID> toppingItemUid,
        UUID productUid,
        Optional<UUID> toppingUid,
        Integer productItemQuantity,
        Optional<Integer> toppingItemPerProductItemQuantity,
        BigDecimal productPrice,
        Optional<BigDecimal> toppingPrice
) {
}
