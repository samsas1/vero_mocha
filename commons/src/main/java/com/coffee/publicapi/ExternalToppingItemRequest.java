package com.coffee.publicapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record ExternalToppingItemRequest(
        @JsonProperty("toppingUid")
        UUID toppingUid,
        @JsonProperty("quantity")
        int quantity
) {
}
