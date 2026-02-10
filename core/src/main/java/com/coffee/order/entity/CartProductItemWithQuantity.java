package com.coffee.order.entity;

import com.coffee.order.entity.database.CartItemTableEntryEntity;

import java.math.BigDecimal;
import java.util.UUID;

public record CartProductItemWithQuantity(
        UUID productItemUid,
        BigDecimal price,
        Integer quantity
) {

    public static CartProductItemWithQuantity fromCartTotalsEntity(CartItemTableEntryEntity entity) {
        return new CartProductItemWithQuantity(
                entity.productItemUid(),
                entity.productPrice(),
                entity.productItemQuantity());
    }

    public BigDecimal getPriceForQuantity() {
        return this.price().multiply(BigDecimal.valueOf(this.quantity()));
    }

}
