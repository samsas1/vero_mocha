package com.coffee;

import com.coffee.admin.*;
import com.coffee.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@RestController
@RequestMapping("/items")
@Validated
public class ItemController {

    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    @Autowired
    private final RestClient coreClient;

    public ItemController(RestClient coreClient) {
        this.coreClient = coreClient;
    }

    @PostMapping("/toppings")
    public ResponseEntity<ToppingResponse> saveTopping(@RequestBody ToppingRequest toppingRequest) {
        log.info("Saving topping: {}", toppingRequest);
        return coreClient.post()
                .uri("/items/toppings")
                .body(toppingRequest)
                .retrieve()
                .toEntity(ToppingResponse.class);
    }

    @GetMapping("/toppings/{uid}")
    public ResponseEntity<ToppingResponse> getTopping(@PathVariable UUID uid) {
        log.info("Getting topping with uid: {}", uid);
        return coreClient.get()
                .uri("/items/toppings/{uid}", uid)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResourceNotFoundException("Topping", "uid", uid);
                })
                .toEntity(ToppingResponse.class);
    }

    @PutMapping("/toppings/{uid}")
    public ResponseEntity<ToppingResponse> updateTopping(@PathVariable UUID uid,
                                                         @RequestBody ToppingRequest request) {
        log.info("Updating topping with uid: {} with request: {}", uid, request);
        return coreClient.put()
                .uri("/items/toppings/{uid}", uid)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResourceNotFoundException("Topping", "uid", uid);
                })
                .toEntity(ToppingResponse.class);
    }

    @GetMapping("/toppings")
    public ResponseEntity<ToppingResponseList> listToppings() {
        log.info("Listing toppings");
        return coreClient.get()
                .uri("/items/toppings")
                .retrieve()
                .toEntity(ToppingResponseList.class);
    }

    @DeleteMapping("/toppings/{uid}")
    public ResponseEntity<Void> deleteTopping(@PathVariable UUID uid) {
        log.info("Deleting topping with uid: {}", uid);
        return coreClient.delete()
                .uri("/items/toppings/{uid}", uid)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResourceNotFoundException("Topping", "uid", uid);
                })
                .toBodilessEntity();
    }

    @PostMapping("/products")
    public ResponseEntity<ProductResponse> saveProduct(@RequestBody ProductRequest request) {
        log.info("Saving product: {}", request);
        return coreClient.post()
                .uri("/items/products")
                .body(request)
                .retrieve()
                .toEntity(ProductResponse.class);
    }

    @GetMapping("/products/{uid}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID uid) {
        log.info("Getting product with uid: {}", uid);
        return coreClient.get()
                .uri("/items/products/{uid}", uid)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResourceNotFoundException("Product", "uid", uid);
                })
                .toEntity(ProductResponse.class);
    }

    @GetMapping("/products")
    public ResponseEntity<ProductResponseList> listProducts() {
        log.info("Listing products");
        return coreClient.get()
                .uri("/items/products")
                .retrieve()
                .toEntity(ProductResponseList.class);
    }

    @PutMapping("/products/{uid}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable UUID uid,
                                                         @RequestBody ProductRequest request) {
        log.info("Updating product with uid: {} with request: {}", uid, request);
        return coreClient.put()
                .uri("/items/products/{uid}", uid)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResourceNotFoundException("Product", "uid", uid);
                })
                .toEntity(ProductResponse.class);
    }

    @DeleteMapping("/products/{uid}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID uid) {
        log.info("Deleting product with uid: {}", uid);
        return coreClient.delete()
                .uri("/items/products/{uid}", uid)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResourceNotFoundException("Product", "uid", uid);
                })
                .toBodilessEntity();
    }
}
