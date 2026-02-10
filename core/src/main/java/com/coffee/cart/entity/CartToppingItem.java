package com.coffee.cart.entity;

import java.math.BigDecimal;
import java.util.UUID;

public record CartToppingItem(
        UUID toppingItemUid,
        UUID toppingUid,
        BigDecimal price,
        Integer quantity) {


    public BigDecimal getPriceForQuantity() {
        return this.price().multiply(BigDecimal.valueOf(this.quantity()));
    }
}
