package com.coffee.publicapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ExternalOrderProductItemResponse(
        @JsonProperty("orderProductItemUid")
        UUID orderProductItemUid,
        @JsonProperty("productUid")
        UUID productUid,
        @JsonProperty("productName")
        String productName,
        @JsonProperty("price")
        BigDecimal price,
        @JsonProperty("quantity")
        Integer quantity,
        @JsonProperty("createdAt")
        Instant createdAt,
        @JsonProperty("toppings")
        List<ExternalOrderToppingItemResponse> toppings
) {
}
