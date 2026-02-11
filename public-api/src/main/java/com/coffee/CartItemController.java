package com.coffee;

import com.coffee.publicapi.ExternalCartItemRequest;
import com.coffee.publicapi.ExternalCartItemResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@RestController
@RequestMapping("/cart")
@Validated
public class CartItemController {

    private static final Logger log = LoggerFactory.getLogger(CartItemController.class);
    private static final String USER_HEADER = "user";

    @Autowired
    private final RestClient coreClient;

    public CartItemController(RestClient coreClient) {
        this.coreClient = coreClient;
    }

    @PostMapping("/items")
    public ResponseEntity<UUID> addToCart(@RequestHeader("user") UUID userUid,
                                          @RequestBody ExternalCartItemRequest cartItemRequest) {
        log.info("Adding item to cart: {} for user: {}", cartItemRequest, userUid);
        return coreClient.post()
                .uri("/cart/items")
                .header(USER_HEADER, userUid.toString())
                .body(cartItemRequest)
                .retrieve()
                .toEntity(UUID.class);
    }

    @GetMapping("/items")
    public ResponseEntity<ExternalCartItemResponse> getCartItems(@RequestHeader("user") UUID userUid) {
        log.info("Getting cart items for user: {}", userUid);
        return coreClient.get()
                .uri("/cart/items")
                .header(USER_HEADER, userUid.toString())
                .retrieve()
                .toEntity(ExternalCartItemResponse.class);
    }

    @DeleteMapping("/items")
    public ResponseEntity<Void> clearCart(@RequestHeader("user") UUID userUid) {
        log.info("Clearing cart for user: {}", userUid);
        return coreClient.get()
                .uri("/cart/items")
                .header(USER_HEADER, userUid.toString())
                .retrieve()
                .toBodilessEntity();
    }
}
