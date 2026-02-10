package com.coffee.order.entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record CartItemMap(
        Map<CartProductItemWithQuantity, List<CartToppingItemWithQuantity>> productsToToppings
) {

    public BigDecimal getTotalOriginalPrice() {
        return this.getTotalProductsPrice().add(this.getTotalToppingsPrice());
    }

    public BigDecimal getTotalProductsPrice() {
        return this.productsToToppings().keySet()
                .stream()
                // Get price for the quantity of products
                .map(CartProductItemWithQuantity::getPriceForQuantity)
                // Sum for all products
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalToppingsPrice() {
        return this.productsToToppings().values()
                .stream()
                // Flatten map into list of toppings
                .flatMap(toppingItemList -> toppingItemList.stream()
                        // Get price for the quantity of topping
                        .map(CartToppingItemWithQuantity::getPriceForQuantity))
                // Sum for all toppings
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
