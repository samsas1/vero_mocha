package com.coffee.cart;

import com.coffee.item.ItemManagementController;
import com.coffee.publicapi.ExternalCartItemRequest;
import com.coffee.publicapi.ExternalCartItemResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cart")
@Validated
public class CartItemController {

    private static final Logger log = LoggerFactory.getLogger(ItemManagementController.class);

    @Autowired
    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @PostMapping("/item")
    public UUID addToCart(@RequestHeader("user") UUID userUid, @RequestBody ExternalCartItemRequest cartItemRequest) {
        return cartItemService.addItemToCart(userUid, cartItemRequest);
    }

    @GetMapping("/item")
    public ExternalCartItemResponse getCartItems(@RequestHeader("user") UUID userUid) {
        return null;
    }
}
