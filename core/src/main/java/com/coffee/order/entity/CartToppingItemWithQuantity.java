package com.coffee.order.entity;

import com.coffee.order.entity.database.CartItemTableEntryEntity;

import java.math.BigDecimal;

public record CartToppingItemWithQuantity(
        BigDecimal price,
        Integer quantity) {
    public static CartToppingItemWithQuantity fromCartTotalsEntity(CartItemTableEntryEntity entity) {
        return new CartToppingItemWithQuantity(
                entity.toppingPrice(),
                entity.toppingItemPerProductItemQuantity());
    }
}
