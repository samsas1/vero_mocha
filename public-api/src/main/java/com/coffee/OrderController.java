package com.coffee;

import com.coffee.publicapi.ExternalOrderResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@RestController
@RequestMapping("/order")
@Validated
public class OrderController {


    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private static final String USER_HEADER = "user";

    @Autowired
    private final RestClient coreClient;

    public OrderController(RestClient coreClient) {
        this.coreClient = coreClient;
    }

    @PostMapping
    public ResponseEntity<UUID> placeOrder(@RequestHeader(USER_HEADER) UUID userUid) {
        log.info("Placing order for user: {}", userUid);
        return coreClient.post()
                .uri("/order")
                .header(USER_HEADER, userUid.toString())
                .retrieve()
                .toEntity(UUID.class);
    }

    @GetMapping
    public ResponseEntity<ExternalOrderResponse> listOrders(@RequestHeader(USER_HEADER) UUID userUid) {
        log.info("Getting orders for user: {}", userUid);
        return coreClient.get()
                .uri("/order")
                .header(USER_HEADER, userUid.toString())
                .retrieve()
                .toEntity(ExternalOrderResponse.class);
    }
}
