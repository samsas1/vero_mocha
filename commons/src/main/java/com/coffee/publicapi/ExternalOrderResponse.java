package com.coffee.publicapi;

import java.util.List;

public record ExternalOrderResponse(
        List<ExternalOrderItemResponse> orders) {
}
