package com.coffee.order;

import com.coffee.order.entity.CartItemMap;
import com.coffee.order.entity.CartProductItemWithQuantity;
import com.coffee.order.entity.CartToppingItemWithQuantity;
import com.coffee.publicapi.ExternalDiscountResult;

import java.math.BigDecimal;
import java.util.Optional;

public interface DiscountHandler {
    Optional<ExternalDiscountResult> handle(CartItemMap cartItemMap);

    default BigDecimal getTotalOriginalPrice(CartItemMap cartItemMap) {
        return getTotalToppingsPrice(cartItemMap).add(getTotalProductsPrice(cartItemMap));
    }

    default BigDecimal getTotalProductsPrice(CartItemMap cartItemMap) {
        return cartItemMap.productsToToppings().keySet()
                .stream()
                // Get price for the quantity of products
                .map(CartProductItemWithQuantity::getPriceForQuantity)
                // Sum for all products
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    default BigDecimal getTotalToppingsPrice(CartItemMap cartItemMap) {
        return cartItemMap.productsToToppings().values()
                .stream()
                // Flatten map into list of toppings
                .flatMap(toppingItemList -> toppingItemList.stream()
                        // Get price for the quantity of topping
                        .map(CartToppingItemWithQuantity::getPriceForQuantity))
                // Sum for all toppings
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
