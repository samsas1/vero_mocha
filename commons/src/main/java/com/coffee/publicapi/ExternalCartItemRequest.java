package com.coffee.publicapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public record ExternalCartItemRequest(
        @JsonProperty("productUid")
        UUID productUid,
        @JsonProperty("quantity")
        int quantity,
        @JsonProperty("toppings")
        List<ExternalToppingItemRequest> toppings) {
}
