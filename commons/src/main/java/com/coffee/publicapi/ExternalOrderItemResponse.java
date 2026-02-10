package com.coffee.publicapi;

import java.util.List;

public record ExternalOrderItemResponse(
        List<ExternalOrderProductItemResponse> orders) {
}
