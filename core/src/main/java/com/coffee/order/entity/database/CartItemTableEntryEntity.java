package com.coffee.order.entity.database;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public record CartItemTableEntryEntity(
        UUID cartUid,
        UUID productItemUid,
        Optional<UUID> toppingItemUid,
        UUID productUid,
        Optional<UUID> toppingUid,
        Integer productItemQuantity,
        Integer toppingItemPerProductItemQuantity,
        BigDecimal productPrice,
        BigDecimal toppingPrice
) {
}
