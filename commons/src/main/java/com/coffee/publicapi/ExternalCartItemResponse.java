package com.coffee.publicapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ExternalCartItemResponse(
        @JsonProperty("items")
        List<ExternalCartProductItemResponse> items
) {
}
