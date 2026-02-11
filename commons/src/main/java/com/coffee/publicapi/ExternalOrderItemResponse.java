package com.coffee.publicapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public record ExternalOrderItemResponse(
        @JsonProperty("orderUid")
        UUID orderUid,
        @JsonProperty("items")
        List<ExternalOrderProductItemResponse> items
) {
}
