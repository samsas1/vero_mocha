package com.coffee.publicapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ExternalCartProductItemResponse(
        @JsonProperty("cartProductItemUid")
        UUID cartProductItemUid,
        @JsonProperty("productUid")
        UUID productUid,
        @JsonProperty("price")
        BigDecimal price,
        @JsonProperty("quantity")
        Integer quantity,
        @JsonProperty("toppings")
        List<ExternalCartToppingItemResponse> toppings
) {
}
