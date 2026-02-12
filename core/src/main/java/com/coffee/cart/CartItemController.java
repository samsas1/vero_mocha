package com.coffee.cart;

import com.coffee.item.ItemManagementController;
import com.coffee.publicapi.ExternalCartItemRequest;
import com.coffee.publicapi.ExternalCartItemResponse;
import com.coffee.publicapi.ExternalDiscountResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for cart operations e.g. placing items in cart, clearing it, checking for eligible discounts.
 *
 * <p>All endpoints require the {@code user} request header that identifies the customer.</p>
 */
@RestController
@RequestMapping("/cart")
@Validated
public class CartItemController {

    private static final Logger log = LoggerFactory.getLogger(ItemManagementController.class);

    @Autowired
    private final CartItemService cartItemService;

    @Autowired
    private final DiscountService discountService;

    public CartItemController(CartItemService cartItemService,
                              DiscountService discountService) {
        this.cartItemService = cartItemService;
        this.discountService = discountService;
    }

    /**
     * Retrieve the current discount applicable to the user's cart.
     *
     * @param userUid user identifier from the {@code user} header
     * @return discount details for the user's cart
     */
    @GetMapping("/discount")
    public ResponseEntity<ExternalDiscountResponse> getCartDiscount(@RequestHeader("user") UUID userUid) {
        log.info("Fetching cart discount for user: {}", userUid);
        return ResponseEntity.ok(discountService.getCartDiscount(userUid));
    }

    /**
     * Add a cart item for the user.
     *
     * @param userUid         user identifier from the {@code user} header
     * @param cartItemRequest item request payload
     * @return identifier of the created cart item
     */
    @PostMapping("/items")
    public ResponseEntity<UUID> addToCart(@RequestHeader("user") UUID userUid, @RequestBody ExternalCartItemRequest cartItemRequest) {
        log.info("Adding item to cart: {} for user: {}", cartItemRequest, userUid);
        return ResponseEntity.ok(cartItemService.addItemToCart(userUid, cartItemRequest));
    }

    /**
     * List the user's cart items.
     *
     * @param userUid user identifier from the {@code user} header
     * @return cart contents for the user
     */
    @GetMapping("/items")
    public ResponseEntity<ExternalCartItemResponse> getCartItems(@RequestHeader("user") UUID userUid) {
        log.info("Getting cart items for user: {}", userUid);
        return ResponseEntity.ok(cartItemService.getCartItems(userUid));
    }

    /**
     * Clear all items from the user's cart.
     *
     * @param userUid user identifier from the {@code user} header
     * @return empty response on success
     */
    @DeleteMapping("/items")
    public ResponseEntity<Void> clearCart(@RequestHeader("user") UUID userUid) {
        log.info("Clearing cart for user: {}", userUid);
        cartItemService.clearCart(userUid);
        return ResponseEntity.ok().build();
    }

    // TODO update cart item
}
