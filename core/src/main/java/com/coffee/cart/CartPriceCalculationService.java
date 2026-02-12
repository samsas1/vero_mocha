package com.coffee.cart;

import com.coffee.cart.entity.CartItem;
import com.coffee.cart.entity.CartItemList;
import com.coffee.cart.entity.CartProductItem;
import com.coffee.cart.entity.CartToppingItem;

import java.math.BigDecimal;

public class CartPriceCalculationService {

    public static BigDecimal getTotalProductsPrice(CartItemList cartItemList) {
        return cartItemList.cartItems()
                .stream()
                // Get products
                .map(CartItem::cartProductItem)
                // Get price for the quantity of products
                .map(CartProductItem::getPriceForQuantity)
                // Sum for all products
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2);
    }

    public static BigDecimal getTotalToppingsPrice(CartItemList cartItemList) {
        return cartItemList.cartItems()
                .stream()
                // Get toppings for each product
                .flatMap(cartItem -> cartItem.cartToppingItemList().stream()
                        // Get price for the quantity of each topping
                        .map(cartToppingItem -> calculatePriceForTopping(
                                cartItem.cartProductItem(),
                                cartToppingItem)))
                // Sum for all toppings
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2);
    }

    public static BigDecimal getTotalCartPrice(CartItemList cartItemList) {
        BigDecimal totalProductsPrice = getTotalProductsPrice(cartItemList);
        BigDecimal totalToppingsPrice = getTotalToppingsPrice(cartItemList);
        return totalProductsPrice.add(totalToppingsPrice).setScale(2);
    }

    private static BigDecimal calculatePriceForTopping(
            CartProductItem cartProductItem,
            CartToppingItem cartToppingItem
    ) {
        BigDecimal toppingPrice = cartToppingItem.price();
        Integer toppingQuantity = cartToppingItem.quantity();
        Integer productQuantity = cartProductItem.quantity();
        // The price of a topping item for a product item needs to multiply both the topping quantity and the product quantity
        //
        return toppingPrice
                .multiply(BigDecimal.valueOf(toppingQuantity))
                .multiply(BigDecimal.valueOf(productQuantity));

    }

    private record CartToppingItemWithProductPrice(
            Integer productQuantity,
            Integer toppingQuantity,
            BigDecimal toppingPrice) {

    }
}
