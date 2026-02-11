package com.coffee.item;

import com.coffee.admin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/items")
@Validated
public class ItemManagementController {

    private static final Logger log = LoggerFactory.getLogger(ItemManagementController.class);
    private final ItemManagementService itemManagementService;

    public ItemManagementController(ItemManagementService itemManagementService) {
        this.itemManagementService = itemManagementService;
    }

    @PostMapping("/toppings")
    public ResponseEntity<ToppingResponse> saveTopping(@RequestBody ToppingRequest toppingDTO) {
        log.info("Received request to save topping: {}", toppingDTO);
        return ResponseEntity.ok(itemManagementService.saveTopping(toppingDTO));
    }

    @GetMapping("/toppings")
    public ResponseEntity<ToppingResponseList> listToppings() {
        log.info("Received request to list toppings");
        return ResponseEntity.ok(itemManagementService.listToppings());
    }

    @GetMapping("/toppings/{uid}")
    public ResponseEntity<ToppingResponse> getTopping(@PathVariable UUID uid) {
        log.info("Received request to get topping with uid: {}", uid);
        return ResponseEntity.ok(itemManagementService.getTopping(uid));
    }

    @PutMapping("/toppings/{uid}")
    public ResponseEntity<ToppingResponse> updateTopping(@PathVariable("uid") UUID toppingUid,
                                                         @RequestBody ToppingRequest toppingDTO) {
        log.info("Received request to update topping with uid: {} and request: {}", toppingUid, toppingDTO);
        return ResponseEntity.ok(itemManagementService.updateTopping(toppingUid, toppingDTO));
    }

    @DeleteMapping("/toppings/{uid}")
    public ResponseEntity<ToppingResponse> deleteTopping(@PathVariable("uid") UUID toppingUid) {
        log.info("Received request to delete topping with uid: {}", toppingUid);
        itemManagementService.deleteTopping(toppingUid);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/products")
    public ResponseEntity<ProductResponse> saveProduct(@RequestBody ProductRequest productDto) {
        log.info("Received request to save product: {}", productDto);
        return ResponseEntity.ok(itemManagementService.saveProduct(productDto));
    }

    @GetMapping("/products")
    public ResponseEntity<ProductResponseList> listProducts() {
        log.info("Received request to list products");
        return ResponseEntity.ok(itemManagementService.listProducts());
    }

    @GetMapping("/products/{uid}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID uid) {
        log.info("Received request to get product with uid: {}", uid);
        return ResponseEntity.ok(itemManagementService.getProduct(uid));
    }

    @PutMapping("/products/{uid}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable("uid") UUID productUid,
                                                         @RequestBody ProductRequest productDTO) {
        log.info("Received request to update product with uid: {} and request: {}", productUid, productDTO);
        return ResponseEntity.ok(itemManagementService.updateProduct(productUid, productDTO));
    }

    @DeleteMapping("/products/{uid}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("uid") UUID productUid) {
        log.info("Received request to delete product with uid: {}", productUid);
        itemManagementService.deleteProduct(productUid);
        return ResponseEntity.ok().build();
    }
}
