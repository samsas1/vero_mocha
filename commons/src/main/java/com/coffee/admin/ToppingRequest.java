package com.coffee.admin;

import com.coffee.enumerators.ExternalItemStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record ToppingRequest(
        @JsonProperty("name")
        String name,
        @JsonProperty("price")
        BigDecimal price,
        @JsonProperty("itemStatus")
        ExternalItemStatus itemStatus
) {
}
