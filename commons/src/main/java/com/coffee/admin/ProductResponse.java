package com.coffee.admin;

import com.coffee.enumerators.ItemStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        @JsonProperty("uid")
        UUID uid,
        @JsonProperty("name")
        String name,
        @JsonProperty("price")
        BigDecimal price,
        @JsonProperty("item_status")
        ItemStatus itemStatus,
        @JsonProperty("createdAt")
        Instant createdAt,
        @JsonProperty("updatedAt")
        Instant updatedAt
) {
}
