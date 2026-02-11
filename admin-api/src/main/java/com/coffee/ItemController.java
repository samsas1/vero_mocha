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
    public ResponseEntity<ToppingResponse> saveTopping(@RequestBody ToppingRequest toppingRequest) {
        log.info("Saving topping: {}", toppingRequest);
        return coreClient.post()
                .uri("/item/topping")
                .body(toppingRequest)
                .retrieve()
                .toEntity(ToppingResponse.class);
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

    @PutMapping("/topping/{uid}")
    public ResponseEntity<ToppingResponse> updateTopping(@PathVariable UUID uid,
                                                         @RequestBody ToppingRequest request) {
        log.info("Updating topping with uid: {} with request: {}", uid, request);
        return coreClient.put()
                .uri("/item/topping/{uid}", uid)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResourceNotFoundException("Topping", "uid", uid);
                })
                .toEntity(ToppingResponse.class);
    }

    @GetMapping("/topping")
    public ResponseEntity<ToppingResponseList> listToppings() {
        log.info("Listing toppings");
        return coreClient.get()
                .uri("/item/topping")
                .retrieve()
                .toEntity(ToppingResponseList.class);
    }

    @DeleteMapping("/topping/{uid}")
    public ResponseEntity<Void> deleteTopping(@PathVariable UUID uid) {
        log.info("Deleting topping with uid: {}", uid);
        return coreClient.delete()
                .uri("/item/topping/{uid}", uid)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResourceNotFoundException("Topping", "uid", uid);
                })
                .toBodilessEntity();
    }

    @PostMapping("/product")
    public ResponseEntity<ProductResponse> saveProduct(@RequestBody ProductRequest request) {
        log.info("Saving product: {}", request);
        return coreClient.post()
                .uri("/item/product")
                .body(request)
                .retrieve()
                .toEntity(ProductResponse.class);
    }

    @GetMapping("/product/{uid}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID uid) {
        log.info("Getting product with uid: {}", uid);
        return coreClient.get()
                .uri("/item/product/{uid}", uid)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResourceNotFoundException("Product", "uid", uid);
                })
                .toEntity(ProductResponse.class);
    }

    @GetMapping("/product")
    public ResponseEntity<ProductResponseList> listProducts() {
        log.info("Listing products");
        return coreClient.get()
                .uri("/item/product")
                .retrieve()
                .toEntity(ProductResponseList.class);
    }

    @PutMapping("/product/{uid}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable UUID uid,
                                                         @RequestBody ProductRequest request) {
        log.info("Updating product with uid: {} with request: {}", uid, request);
        return coreClient.put()
                .uri("/item/product/{uid}", uid)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResourceNotFoundException("Product", "uid", uid);
                })
                .toEntity(ProductResponse.class);
    }

    @DeleteMapping("/product/{uid}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID uid) {
        log.info("Deleting product with uid: {}", uid);
        return coreClient.delete()
                .uri("/item/product/{uid}", uid)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResourceNotFoundException("Product", "uid", uid);
                })
                .toBodilessEntity();
    }
}
