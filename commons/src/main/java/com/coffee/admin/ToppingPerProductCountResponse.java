package com.coffee.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record ToppingPerProductCountResponse(
        @JsonProperty("toppingUid")
        UUID toppingUid,
        @JsonProperty("toppingName")
        String toppingName,
        @JsonProperty("totalOrderedForProduct")
        Integer totalOrderedForProduct,
        @JsonProperty("averageOrderedForProduct")
        BigDecimal averageOrderedForProduct
) {
}
