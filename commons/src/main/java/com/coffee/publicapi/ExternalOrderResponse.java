package com.coffee.publicapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ExternalOrderResponse(
        @JsonProperty("orders")
        List<ExternalOrderItemResponse> orders) {
}
