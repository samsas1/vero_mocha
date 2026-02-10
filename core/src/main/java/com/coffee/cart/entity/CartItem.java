package com.coffee.cart.entity;

import java.util.List;

public record CartItem(
        CartProductItem cartProductItem,
        List<CartToppingItem> cartToppingItemList
) {
}
