package com.coffee.order;

import com.coffee.publicapi.ExternalOrderPlacementResponse;
import com.coffee.publicapi.ExternalOrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/order")
@Validated
public class OrderContoller {

    @Autowired
    private final OrderService orderService;

    public OrderContoller(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ExternalOrderPlacementResponse> placeOrder(@RequestHeader("user") UUID userUid) {
        return ResponseEntity.ok(orderService.placeOrder(userUid));
    }

    @GetMapping
    public ResponseEntity<ExternalOrderResponse> listOrders(@RequestHeader("user") UUID userUid) {
        return ResponseEntity.ok(orderService.listOrders(userUid));
    }

    // TODO getOrder
}
