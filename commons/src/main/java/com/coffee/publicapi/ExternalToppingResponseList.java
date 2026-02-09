package com.coffee.publicapi;

import java.util.List;

public record ExternalToppingResponseList(
        List<ExternalToppingResponse> toppings
) {
}
