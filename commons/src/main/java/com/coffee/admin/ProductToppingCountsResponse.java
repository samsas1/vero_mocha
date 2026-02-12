package com.coffee.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ProductToppingCountsResponse(
        @JsonProperty("productToppingCounts")
        List<ProductToppingCountResponse> productToppingCounts) {
}
