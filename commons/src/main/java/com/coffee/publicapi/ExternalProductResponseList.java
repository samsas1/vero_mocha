package com.coffee.publicapi;

import java.util.List;

public record ExternalProductResponseList(
        List<ExternalProductResponse> products
) {
}
