package com.coffee.cart.entity;

import com.coffee.cart.entity.database.CartProductItemEntity;
import com.coffee.cart.entity.database.CartToppingItemEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


public record CartItemList(
        List<CartItem> cartItems
) {

    // TODO extract the mapping to a class to properly test
    public static CartItemList fromCartItemEntities(
            List<CartProductItemEntity> cartProductItemEntities,
            List<CartToppingItemEntity> cartToppingItemEntities) {
        // Generate a map of product item uuid to pertinent topping item information
        Map<UUID, List<CartToppingItem>> productItemsToToppings = cartToppingItemEntities
                .stream()
                .collect(Collectors.groupingBy(
                        topping -> topping.getCartProductItem().getUid(),
                        Collectors.mapping(
                                topping -> new CartToppingItem(
                                        topping.getUid(),
                                        topping.getTopping().getUid(),
                                        topping.getTopping().getName(),
                                        topping.getTopping().getPrice(),
                                        topping.getQuantity()
                                ),
                                Collectors.toList()
                        )
                ));

        // Use the map above and iterate over product items to combine the product items
        // and topping items into cart item objects
        List<CartItem> cartItems = cartProductItemEntities
                .stream()
                .map(o -> new CartProductItem(
                        o.getUid(),
                        o.getProduct().getUid(),
                        o.getProduct().getName(),
                        o.getProduct().getPrice(),
                        o.getQuantity()
                ))
                .map(
                        o -> new CartItem(
                                o,
                                productItemsToToppings.getOrDefault(o.productItemUid(), List.of())
                        )
                )
                .toList();
        return new CartItemList(cartItems);
    }
}
