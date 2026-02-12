package com.coffee.publicapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record ExternalOrderPlacementResponse(
        @JsonProperty("orderUid")
        UUID orderUid,
        @JsonProperty("originalPrice")
        BigDecimal originalPrice,
        @JsonProperty("finalPrice")
        BigDecimal finalPrice,
        @JsonProperty("placed")
        Boolean placed,
        @JsonProperty("message")
        String message
) {

    private static final String EMPTY_CART_MESSAGE = "No order created. Cart is empty.";

    public static ExternalOrderPlacementResponse empty() {
        return new ExternalOrderPlacementResponse(null, null, null, false, EMPTY_CART_MESSAGE);
    }

}
