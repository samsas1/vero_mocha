package com.coffee.order;

import com.coffee.publicapi.ExternalOrderItemResponse;
import com.coffee.publicapi.ExternalOrderPlacementResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ExternalOrderPlacementResponse placeOrder(@RequestHeader("user") UUID userUid) {
        return orderService.placeOrder(userUid);
    }

    @GetMapping

    public ExternalOrderItemResponse listOrders(@RequestHeader("user") UUID userUid) {
        return orderService.listOrders(userUid);
    }
}
