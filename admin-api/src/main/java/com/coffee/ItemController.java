package com.coffee;

import com.coffee.admin.ProductRequest;
import com.coffee.admin.ProductResponse;
import com.coffee.admin.ToppingRequest;
import com.coffee.admin.ToppingResponse;
import com.coffee.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/item")
@Validated
public class ItemController {

    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    @Autowired
    private final RestClient coreClient;

    public ItemController(RestClient coreClient) {
        this.coreClient = coreClient;
    }

    @PostMapping("/topping")
    public ResponseEntity<UUID> saveTopping(@RequestBody ToppingRequest toppingRequest) {
        log.info("Saving topping: {}", toppingRequest);
        return coreClient.post()
                .uri("/item/topping")
                .body(toppingRequest)
                .retrieve()
                .toEntity(UUID.class);
    }

    @GetMapping("/topping/{uid}")
    public ResponseEntity<ToppingResponse> getTopping(@PathVariable UUID uid) {
        log.info("Getting topping with uid: {}", uid);
        return coreClient.get()
                .uri("/item/topping/{uid}", uid)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResourceNotFoundException("Topping", "uid", uid);
                })
                .toEntity(ToppingResponse.class);
    }

    @GetMapping("/topping")
    public ResponseEntity<List<ToppingResponse>> listToppings() {
        log.info("Listing toppings");
        return coreClient.get()
                .uri("/item/topping")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    @PostMapping("/product")
    public ResponseEntity<UUID> saveProduct(@RequestBody ProductRequest request) {
        log.info("Saving product: {}", request);
        return coreClient.post()
                .uri("/item/product")
                .body(request)
                .retrieve()
                .toEntity(UUID.class);
    }

    @GetMapping("/product/{uid}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID uid) {
        log.info("Getting product with uid: {}", uid);
        return coreClient.get()
                .uri("/item/product/{uid}", uid)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResourceNotFoundException("Topping", "uid", uid);
                })
                .toEntity(ProductResponse.class);
    }

    @GetMapping("/product")
    public ResponseEntity<List<ProductResponse>> listProducts() {
        log.info("Listing products");
        return coreClient.get()
                .uri("/item/product")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }
}
