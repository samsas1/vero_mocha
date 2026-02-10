package com.coffee.order;

import com.coffee.order.entity.CartItemMap;
import com.coffee.publicapi.ExternalDiscountResponse;

import java.util.Optional;

public interface DiscountHandler {
    Optional<ExternalDiscountResponse> handle(CartItemMap cartItemMap);


}
