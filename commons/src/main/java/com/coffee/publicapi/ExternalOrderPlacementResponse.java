package com.coffee.publicapi;

import java.math.BigDecimal;
import java.util.UUID;

public record ExternalOrderPlacementResponse(
        UUID orderUid,
        BigDecimal originalPrice,
        BigDecimal finalPrice
) {
}
