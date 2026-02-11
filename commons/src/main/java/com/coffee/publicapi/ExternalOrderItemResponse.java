package com.coffee.publicapi;

import java.util.List;
import java.util.UUID;

public record ExternalOrderItemResponse(
        UUID uid,
        List<ExternalOrderProductItemResponse> orders
) {
}
