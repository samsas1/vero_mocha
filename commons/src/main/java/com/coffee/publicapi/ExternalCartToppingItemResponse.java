package com.coffee.publicapi;

import java.math.BigDecimal;
import java.util.UUID;

public record ExternalCartToppingItemResponse(
        UUID cartToppingItemUid,
        UUID toppingUid,
        BigDecimal price,
        Integer quantity) {
}
