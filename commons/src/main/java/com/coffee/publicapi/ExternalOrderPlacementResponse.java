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
    public static ExternalOrderPlacementResponse empty() {
        return new ExternalOrderPlacementResponse(null, null, null, null, null);
    }

}
