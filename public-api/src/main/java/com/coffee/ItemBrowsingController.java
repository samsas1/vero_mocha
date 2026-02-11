package com.coffee;

import com.coffee.publicapi.ExternalProductResponseList;
import com.coffee.publicapi.ExternalToppingResponseList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@RestController
@RequestMapping("/menu")
@Validated
public class ItemBrowsingController {


    private static final Logger log = LoggerFactory.getLogger(ItemBrowsingController.class);
    private static final String USER_HEADER = "user";

    @Autowired
    private final RestClient coreClient;

    public ItemBrowsingController(RestClient coreClient) {
        this.coreClient = coreClient;
    }

    @GetMapping("/toppings")
    public ResponseEntity<ExternalToppingResponseList> listActiveToppings(@RequestHeader("user") UUID userUid) {
        log.info("Received request to list active toppings from user: {}", userUid);
        return coreClient.get()
                .uri("/menu/toppings")
                .header(USER_HEADER, userUid.toString())
                .retrieve()
                .toEntity(ExternalToppingResponseList.class);
    }

    @GetMapping("/products")
    public ResponseEntity<ExternalProductResponseList> getCartItems(@RequestHeader("user") UUID userUid) {
        log.info("Received request to list active products from user: {}", userUid);
        return coreClient.get()
                .uri("/menu/products")
                .header(USER_HEADER, userUid.toString())
                .retrieve()
                .toEntity(ExternalProductResponseList.class);
    }
}
