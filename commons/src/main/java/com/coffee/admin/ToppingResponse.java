package com.coffee.admin;

import com.coffee.enumerators.ItemStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ToppingResponse(
        @JsonProperty("uid")
        UUID uid,
        @JsonProperty("name")
        String name,
        @JsonProperty("price")
        BigDecimal price,
        @JsonProperty("itemStatus")
        ItemStatus itemStatus,
        @JsonProperty("createdAt")
        Instant createdAt,
        @JsonProperty("updatedAt")
        Instant updatedAt
) {
}
