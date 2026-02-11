package com.coffee.publicapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record ExternalOrderToppingItemResponse(
        @JsonProperty("orderToppingItemUid")
        UUID orderToppingItemUid,
        @JsonProperty("toppingUid")
        UUID toppingUid,
        @JsonProperty("price")
        BigDecimal price,
        @JsonProperty("quantity")
        Integer quantity
) {
}
