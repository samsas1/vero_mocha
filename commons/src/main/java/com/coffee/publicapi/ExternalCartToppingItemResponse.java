package com.coffee.publicapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record ExternalCartToppingItemResponse(
        @JsonProperty("cartToppingItemUid")
        UUID cartToppingItemUid,
        @JsonProperty("toppingUid")
        UUID toppingUid,
        @JsonProperty("toppingName")
        String toppingName,
        @JsonProperty("price")
        BigDecimal price,
        @JsonProperty("quantity")
        Integer quantity) {
}
