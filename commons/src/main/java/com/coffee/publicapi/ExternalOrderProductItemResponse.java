package com.coffee.publicapi;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ExternalOrderProductItemResponse(
        UUID orderProductItemUid,
        UUID productUid,
        BigDecimal price,
        Integer quantity,
        Instant createdAt,
        List<ExternalOrderToppingItemResponse> toppings
) {
}
