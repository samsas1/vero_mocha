package com.coffee.publicapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ExternalProductResponse(
        @JsonProperty("uid")
        UUID uid,
        @JsonProperty("name")
        String name,
        @JsonProperty("price")
        BigDecimal price,
        @JsonProperty("createdAt")
        Instant createdAt,
        @JsonProperty("updatedAt")
        Instant updatedAt
) {
}
