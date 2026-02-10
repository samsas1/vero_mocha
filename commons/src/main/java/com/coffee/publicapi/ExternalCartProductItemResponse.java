package com.coffee.publicapi;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ExternalCartProductItemResponse(
        UUID cartProductItemUid,
        UUID productUid,
        BigDecimal price,
        Integer quantity,
        List<ExternalCartToppingItemResponse> toppings
) {
}
