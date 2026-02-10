package com.coffee.order.entity;

import java.util.List;
import java.util.Map;

public record CartItemMap(
        Map<CartProductItemWithQuantity, List<CartToppingItemWithQuantity>> productsToToppings
) {
}
