package com.coffee.order;

import com.coffee.publicapi.ExternalOrderPlacementResponse;
import com.coffee.publicapi.ExternalOrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for placing and fetching orders.
 *
 * <p>All endpoints require the {@code user} request header that identifies the customer.</p>
 */
@RestController
@RequestMapping("/orders")
@Validated
public class OrderContoller {

    @Autowired
    private final OrderService orderService;

    public OrderContoller(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Place a new order for the user.
     * This action will clear the user's cart and create a new order with the current cart items and applicable discounts.
     *
     * @param userUid user identifier from the {@code user} header
     * @return placement response containing order details
     */
    @PostMapping
    public ResponseEntity<ExternalOrderPlacementResponse> placeOrder(@RequestHeader("user") UUID userUid) {
        return ResponseEntity.ok(orderService.placeOrder(userUid));
    }

    /**
     * List all orders for the user.
     *
     * @param userUid user identifier from the {@code user} header
     * @return list of orders for the user
     */
    @GetMapping
    public ResponseEntity<ExternalOrderResponse> listOrders(@RequestHeader("user") UUID userUid) {
        return ResponseEntity.ok(orderService.listOrders(userUid));
    }

    // TODO getOrder
}
