package com.coffee.publicapi;

import java.util.List;

public record ExternalDiscountResponse(
        List<ExternalDiscountResult> discounts
) {
}
