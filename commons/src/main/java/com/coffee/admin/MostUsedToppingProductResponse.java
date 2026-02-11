package com.coffee.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record MostUsedToppingProductResponse(
        @JsonProperty("productUid")
        UUID productUid,
        @JsonProperty("productName")
        String productName,
        @JsonProperty("toppingUid")
        UUID toppingUid,
        @JsonProperty("toppingName")
        String toppingName,
        @JsonProperty("totalOrderedForProduct")
        int totalOrderedForProduct
) {
}
