package com.coffee.publicapi;

import java.math.BigDecimal;
import java.util.UUID;

public record ExternalDiscountResult(
        ExternalDiscountType discountType,
        BigDecimal originalPrice,
        BigDecimal finalPrice,
        UUID discountedProductUid
) {
}
