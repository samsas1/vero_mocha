package com.coffee.publicapi;

import java.math.BigDecimal;
import java.util.UUID;

public record ExternalOrderToppingItemResponse(
        UUID orderToppingItemUid,
        UUID toppingUid,
        BigDecimal price,
        Integer quantity
) {
}
