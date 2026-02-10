package com.coffee.publicapi;

import java.util.UUID;

public record ExternalToppingItemRequest(
        UUID toppingUid,
        int quantity
) {
}
