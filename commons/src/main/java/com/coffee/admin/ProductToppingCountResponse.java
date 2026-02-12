package com.coffee.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public record ProductToppingCountResponse(
        @JsonProperty("productUid")
        UUID productUid,
        @JsonProperty("productName")
        String productName,
        @JsonProperty("totalOrdered")
        Integer totalOrdered,
        @JsonProperty("toppingCounts")
        List<ToppingPerProductCountResponse> toppingCounts) {
}
