package com.coffee.cart;


import com.coffee.cart.batch.CartToppingItemBatchRepository.CartTopping;
import com.coffee.publicapi.ExternalCartItemRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CartItemService {

    private static final Logger log = LoggerFactory.getLogger(CartItemService.class);

    @Autowired
    private CartProductItemRepository cartProductItemRepository;

    @Autowired
    private CartToppingItemRepository cartToppingItemRepository;

    public CartItemService(CartProductItemRepository cartProductItemRepository, CartToppingItemRepository cartToppingItemRepository) {
        this.cartProductItemRepository = cartProductItemRepository;
        this.cartToppingItemRepository = cartToppingItemRepository;
    }

    public UUID addItemToCart(UUID userUid, ExternalCartItemRequest cartItemRequest) {
        log.debug("Adding item request: {} to cart for user {}", cartItemRequest, userUid);
        UUID cartProductItemUid = UUID.randomUUID();
        Instant createdAt = Instant.now();
        cartProductItemRepository.saveCartProduct(
                cartProductItemUid,
                userUid,
                cartItemRequest.productUid(),
                cartItemRequest.quantity(),
                createdAt
        );

        if (cartItemRequest.toppings().isEmpty()) {
            log.debug("No toppings present in request for user: {}", userUid);
            return cartProductItemUid;
        }

        // TODO validate cart toppings are not repeated per cart item
        // I.E. [{topping1,quantity=2}, {topping1, quantity=3}]
        List<CartTopping> cartToppings = cartItemRequest.toppings()
                .stream().map(
                        o -> new CartTopping(
                                UUID.randomUUID(),
                                cartProductItemUid,
                                o.toppingUid(),
                                o.quantity(),
                                createdAt
                        )
                )
                .toList();

        cartToppingItemRepository.saveCartProduct(cartToppings);
        return cartProductItemUid;
    }

    public UUID updateProductToppings() {
        //TODO handle existing topping updates
        return null;
    }
}
