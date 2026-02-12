package com.coffee.cart;


import com.coffee.cart.custom.query.batch.CartToppingItemBatchRepository.CartToppingItemRecord;
import com.coffee.cart.entity.CartItem;
import com.coffee.cart.entity.CartItemList;
import com.coffee.cart.entity.CartToppingItem;
import com.coffee.cart.entity.database.CartProductItemEntity;
import com.coffee.cart.entity.database.CartToppingItemEntity;
import com.coffee.publicapi.ExternalCartItemRequest;
import com.coffee.publicapi.ExternalCartItemResponse;
import com.coffee.publicapi.ExternalCartProductItemResponse;
import com.coffee.publicapi.ExternalCartToppingItemResponse;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.coffee.cart.entity.CartItemList.fromCartItemEntities;

@Service
@Transactional
public class CartItemService {

    private static final Logger log = LoggerFactory.getLogger(CartItemService.class);

    @Autowired
    private CartProductItemRepository cartProductItemRepository;

    @Autowired
    private CartToppingItemRepository cartToppingItemRepository;

    public CartItemService(CartProductItemRepository cartProductItemRepository,
                           CartToppingItemRepository cartToppingItemRepository) {
        this.cartProductItemRepository = cartProductItemRepository;
        this.cartToppingItemRepository = cartToppingItemRepository;
    }

    public UUID addItemToCart(UUID userUid, ExternalCartItemRequest cartItemRequest) {
        // TODO validate product and toppings exist for cart item request
        // Ideally would be done with a call to ItemBrowsingService
        // This would allow a flow that would return a more informative response more easily
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
        List<CartToppingItemRecord> cartToppingItems = cartItemRequest.toppings()
                .stream().map(
                        o -> new CartToppingItemRecord(
                                UUID.randomUUID(),
                                cartProductItemUid,
                                o.toppingUid(),
                                o.quantity(),
                                createdAt
                        )
                )
                .toList();

        cartToppingItemRepository.saveCartProduct(cartToppingItems);
        return cartProductItemUid;
    }

    public void clearCart(UUID userUid) {
        cartProductItemRepository.deleteCartItems(userUid);
    }

    public CartItemList getCartItemList(UUID userUid) {
        List<CartProductItemEntity> cartProductEntityItems = cartProductItemRepository
                .getCartProductItemEntitiesByCart_UserUid(userUid);
        List<CartToppingItemEntity> cartToppingItemEntities = cartToppingItemRepository
                .getCartToppingItemEntitiesByCartProductItemIn(cartProductEntityItems);
        return fromCartItemEntities(cartProductEntityItems, cartToppingItemEntities);
    }

    public ExternalCartItemResponse getCartItems(UUID userUid) {
        CartItemList cartItemList = getCartItemList(userUid);
        return map(cartItemList);
    }

    public boolean isCartEmpty(UUID userUid) {
        return cartProductItemRepository.isCartEmpty(userUid);
    }

    private ExternalCartItemResponse map(CartItemList cartItemList) {
        return new ExternalCartItemResponse(
                cartItemList.cartItems()
                        .stream()
                        .map(this::map).toList()
        );
    }

    private ExternalCartProductItemResponse map(CartItem cartItem) {
        return new ExternalCartProductItemResponse(
                cartItem.cartProductItem().productItemUid(),
                cartItem.cartProductItem().productUid(),
                cartItem.cartProductItem().price(),
                cartItem.cartProductItem().quantity(),
                cartItem.cartToppingItemList().stream()
                        .map(this::map).toList()
        );
    }

    private ExternalCartToppingItemResponse map(CartToppingItem cartToppingItem) {
        return new ExternalCartToppingItemResponse(
                cartToppingItem.toppingItemUid(),
                cartToppingItem.toppingUid(),
                cartToppingItem.price(),
                cartToppingItem.quantity()
        );
    }
}
