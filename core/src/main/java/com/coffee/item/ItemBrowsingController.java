package com.coffee.item;

import com.coffee.publicapi.ExternalProductResponseList;
import com.coffee.publicapi.ExternalToppingResponseList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for browsing products and items available to the public.
 */
@RestController
@RequestMapping("/menu")
@Validated
public class ItemBrowsingController {

    private static final Logger log = LoggerFactory.getLogger(ItemManagementController.class);
    private final ItemBrowsingService itemBrowsingService;

    public ItemBrowsingController(ItemBrowsingService itemBrowsingService) {
        this.itemBrowsingService = itemBrowsingService;
    }

    /**
     * List active toppings that can be applied to products.
     *
     * @return list of active toppings
     */
    @GetMapping("/toppings")
    public ResponseEntity<ExternalToppingResponseList> listActiveToppings() {
        log.info("Received request to list active toppings");
        return ResponseEntity.ok(itemBrowsingService.listActiveToppings());
    }

    /**
     * List active products available on the menu.
     *
     * @return list of active products
     */
    @GetMapping("/products")
    public ResponseEntity<ExternalProductResponseList> listActiveProducts() {
        log.info("Received request to list active products");
        return ResponseEntity.ok(itemBrowsingService.listActiveProducts());
    }

    // TODO get product/get topping

}
