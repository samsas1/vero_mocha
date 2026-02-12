package com.coffee.item;

import com.coffee.admin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for managing products and toppings in the catalog.
 */
@RestController
@RequestMapping("/items")
@Validated
public class ItemManagementController {

    private static final Logger log = LoggerFactory.getLogger(ItemManagementController.class);
    private final ItemManagementService itemManagementService;

    public ItemManagementController(ItemManagementService itemManagementService) {
        this.itemManagementService = itemManagementService;
    }

    /**
     * Create a new topping.
     *
     * @param toppingDTO topping request payload
     * @return created topping details
     */
    @PostMapping("/toppings")
    public ResponseEntity<ToppingResponse> saveTopping(@RequestBody ToppingRequest toppingDTO) {
        log.info("Received request to save topping: {}", toppingDTO);
        return ResponseEntity.ok(itemManagementService.saveTopping(toppingDTO));
    }

    /**
     * List all toppings.
     * Unlike the browsing endpoint, this includes non-active toppings as well.
     *
     * @return list of toppings
     */
    @GetMapping("/toppings")
    public ResponseEntity<ToppingResponseList> listToppings() {
        log.info("Received request to list toppings");
        return ResponseEntity.ok(itemManagementService.listToppings());
    }

    /**
     * Fetch a single topping by id.
     *
     * @param uid topping identifier
     * @return topping details
     */
    @GetMapping("/toppings/{uid}")
    public ResponseEntity<ToppingResponse> getTopping(@PathVariable UUID uid) {
        log.info("Received request to get topping with uid: {}", uid);
        return ResponseEntity.ok(itemManagementService.getTopping(uid));
    }

    /**
     * Update an existing topping by id.
     *
     * @param toppingUid topping identifier
     * @param toppingDTO topping request payload
     * @return updated topping details
     */
    @PutMapping("/toppings/{uid}")
    public ResponseEntity<ToppingResponse> updateTopping(@PathVariable("uid") UUID toppingUid,
                                                         @RequestBody ToppingRequest toppingDTO) {
        log.info("Received request to update topping with uid: {} and request: {}", toppingUid, toppingDTO);
        return ResponseEntity.ok(itemManagementService.updateTopping(toppingUid, toppingDTO));
    }

    /**
     * Delete a topping by id.
     * Note, this endpoint will fail if the topping has orders or is added to cart.
     * The proper action in this case is to mark the topping as INACTIVE, which can be done through the update endpoint.
     *
     * @param toppingUid topping identifier
     * @return empty response on success
     */
    @DeleteMapping("/toppings/{uid}")
    public ResponseEntity<ToppingResponse> deleteTopping(@PathVariable("uid") UUID toppingUid) {
        log.info("Received request to delete topping with uid: {}", toppingUid);
        itemManagementService.deleteTopping(toppingUid);
        return ResponseEntity.ok().build();
    }


    /**
     * Create a new product.
     *
     * @param productDto product request payload
     * @return created product details
     */
    @PostMapping("/products")
    public ResponseEntity<ProductResponse> saveProduct(@RequestBody ProductRequest productDto) {
        log.info("Received request to save product: {}", productDto);
        return ResponseEntity.ok(itemManagementService.saveProduct(productDto));
    }

    /**
     * List all products.
     * Unlike the browsing endpoint, this includes non-active products as well.
     *
     * @return list of products
     */
    @GetMapping("/products")
    public ResponseEntity<ProductResponseList> listProducts() {
        log.info("Received request to list products");
        return ResponseEntity.ok(itemManagementService.listProducts());
    }

    /**
     * Fetch a single product by id.
     *
     * @param uid product identifier
     * @return product details
     */
    @GetMapping("/products/{uid}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID uid) {
        log.info("Received request to get product with uid: {}", uid);
        return ResponseEntity.ok(itemManagementService.getProduct(uid));
    }

    /**
     * Update an existing product by id.
     *
     * @param productUid product identifier
     * @param productDTO product request payload
     * @return updated product details
     */
    @PutMapping("/products/{uid}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable("uid") UUID productUid,
                                                         @RequestBody ProductRequest productDTO) {
        log.info("Received request to update product with uid: {} and request: {}", productUid, productDTO);
        return ResponseEntity.ok(itemManagementService.updateProduct(productUid, productDTO));
    }

    /**
     * Delete a product by id.
     * Note, this endpoint will fail if the topping has orders or is added to cart.
     * The proper action in this case is to mark the product as INACTIVE, which can be done through the update endpoint.
     *
     * @param productUid product identifier
     * @return empty response on success
     */
    @DeleteMapping("/products/{uid}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("uid") UUID productUid) {
        log.info("Received request to delete product with uid: {}", productUid);
        itemManagementService.deleteProduct(productUid);
        return ResponseEntity.ok().build();
    }
}
