package com.coffee.publicapi;

import java.util.List;

public record ExternalCartItemResponse(
        List<ExternalCartProductItemResponse> items
) {
}
