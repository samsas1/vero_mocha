package com.coffee.publicapi;

import java.math.BigDecimal;

public record ExternalDiscountResponse(
        ExternalDiscountType discountType,
        BigDecimal originalPrice,
        BigDecimal finalPrice
) {
}
