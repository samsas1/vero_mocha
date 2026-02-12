package com.coffee.cart.entity;

import java.util.List;

/**
 * Represents an item in the cart.
 * The cart item has a product item as a base (e.g. a latte) which is placed in a cart with some quantity (e.g. 3 lattes).
 * <p>
 * In addition, the product item can have a list of toppings (e.g. extra shot, soy milk)
 * The toppings are also placed in the cart with some quantity (e.g. 2 extra shots, 1 soy milk).
 * <p>
 * The quantity of the product item determines the total quantity of the topping items.
 * For example, ordering 2 lattes with an extra shot will result in a flattened order of 2 lattes and 2 extra shots, as the extra shot is applied to each latte. And the final order becomes {1 latte + 1 extra shot, 1 latte + 1 extra shot}.
 *
 * @param cartProductItem
 * @param cartToppingItemList
 */
public record CartItem(
        CartProductItem cartProductItem,
        List<CartToppingItem> cartToppingItemList
) {
}
