package com.coffee.admin;

import com.coffee.enumerators.ItemStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record ToppingRequest(
        @JsonProperty("name")
        String name,
        @JsonProperty("price")
        BigDecimal price,
        @JsonProperty("itemStatus")
        ItemStatus itemStatus
) {
}
