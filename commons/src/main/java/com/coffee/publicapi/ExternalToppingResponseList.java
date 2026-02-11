package com.coffee.publicapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ExternalToppingResponseList(
        @JsonProperty("toppings")
        List<ExternalToppingResponse> toppings
) {
}
