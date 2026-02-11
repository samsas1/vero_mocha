package com.coffee.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MostUsedProductToppingResponse(
        @JsonProperty("mostUsedPerProduct")
        List<MostUsedToppingProductResponse> mostUsedToppingsPerProduct) {
}
