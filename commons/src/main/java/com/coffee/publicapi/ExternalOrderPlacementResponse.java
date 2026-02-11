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
        BigDecimal finalPrice
) {
}
