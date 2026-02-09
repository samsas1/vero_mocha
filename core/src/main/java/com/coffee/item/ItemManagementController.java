package com.coffee.item;

import com.coffee.admin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/item")
@Validated
public class ItemManagementController {

    private static final Logger log = LoggerFactory.getLogger(ItemManagementController.class);
    private final ItemManagementService itemManagementService;

    public ItemManagementController(ItemManagementService itemManagementService) {
        this.itemManagementService = itemManagementService;
    }

    @PostMapping("/topping")
    public ResponseEntity<UUID> saveTopping(@RequestBody ToppingRequest toppingDTO) {
        log.info("Received request to save topping: {}", toppingDTO);
        return ResponseEntity.ok(itemManagementService.saveTopping(toppingDTO));
    }

    @GetMapping("/topping")
    public ResponseEntity<ToppingResponseList> listToppings() {
        log.info("Received request to list toppings");
        return ResponseEntity.ok(itemManagementService.listToppings());
    }

    @GetMapping("/topping/{uid}")
    public ResponseEntity<ToppingResponse> getTopping(@PathVariable UUID uid) {
        log.info("Received request to get topping with uid: {}", uid);
        return ResponseEntity.ok(itemManagementService.getTopping(uid));
    }

    @PostMapping("/product")
    public ResponseEntity<UUID> saveProduct(@RequestBody ProductRequest productDto) {
        log.info("Received request to save product: {}", productDto);
        return ResponseEntity.ok(itemManagementService.saveProduct(productDto));
    }

    @GetMapping("/product")
    public ResponseEntity<ProductResponseList> listProducts() {
        log.info("Received request to list products");
        return ResponseEntity.ok(itemManagementService.listProducts());
    }

    @GetMapping("/product/{uid}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID uid) {
        log.info("Received request to get product with uid: {}", uid);
        return ResponseEntity.ok(itemManagementService.getProduct(uid));
    }

    //TODO update and delete endpoints for both product and topping
}
