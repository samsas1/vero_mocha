package com.coffee.publicapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ExternalProductResponseList(
        @JsonProperty("products")
        List<ExternalProductResponse> products
) {
}
