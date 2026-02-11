package com.coffee.publicapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record ExternalDiscountResponse(
        @JsonProperty("discountType")
        ExternalDiscountType discountType,
        @JsonProperty("originalPrice")
        BigDecimal originalPrice,
        @JsonProperty("finalPrice")
        BigDecimal finalPrice
) {
}
