package com.coffee.order;

import com.coffee.publicapi.ExternalOrderPlacementResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
