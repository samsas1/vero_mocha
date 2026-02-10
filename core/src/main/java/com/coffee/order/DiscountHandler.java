package com.coffee.order;

import com.coffee.cart.entity.CartItemList;
import com.coffee.publicapi.ExternalDiscountResponse;

import java.util.Optional;

public interface DiscountHandler {
    Optional<ExternalDiscountResponse> handle(CartItemList cartItemList);


}
