package com.coffee.publicapi;

import java.util.List;
import java.util.UUID;

public record ExternalCartItemRequest(
        UUID productUid,
        int quantity,
        List<ExternalToppingItemRequest> toppings) {
}
