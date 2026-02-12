package com.coffee.publicapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public record ExternalCartItemResponse(
        @JsonProperty("totalPrice")
        BigDecimal totalPrice,
        @JsonProperty("items")
        List<ExternalCartProductItemResponse> items
) {
}
