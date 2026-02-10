package com.coffee.cart.order;

import com.coffee.order.FullCartDiscountHandler;
import com.coffee.order.entity.CartItemMap;
import com.coffee.order.entity.CartProductItemWithQuantity;
import com.coffee.order.entity.CartToppingItemWithQuantity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.assertj.core.api.Assertions.assertThat;

public class FullCartDiscountHandlerTest {
    private final BigDecimal discountMultiplier = BigDecimal.valueOf(0.75);
    private final BigDecimal discountThreshold = BigDecimal.valueOf(12);

    // Cheapest product item
    private CartProductItemWithQuantity productItemCheapest;
    private CartToppingItemWithQuantity toppingItemCheapest1;
    private CartToppingItemWithQuantity toppingItemCheapest2;
    private CartToppingItemWithQuantity toppingItemCheapest3;
    private Entry<CartProductItemWithQuantity, List<CartToppingItemWithQuantity>> cheapestEntry;
    private BigDecimal totalProductItemCheapestPrice;

    // Product item with free topping
    private CartProductItemWithQuantity productItemFreeTopping;
    private CartToppingItemWithQuantity toppingItemFreeTopping;
    private Entry<CartProductItemWithQuantity, List<CartToppingItemWithQuantity>> freeToppingEntry;
    private BigDecimal totalProductItemFreeToppingPrice;


    // Product item with free product
    private CartProductItemWithQuantity productItemFreeProduct;
    private CartToppingItemWithQuantity toppingItemFreeProduct;
    private Entry<CartProductItemWithQuantity, List<CartToppingItemWithQuantity>> freeProductEntry;
    private BigDecimal totalProductItemFreeProductPrice;

    // Product item with no toppings
    private CartProductItemWithQuantity productItemNoTopping;
    private Entry<CartProductItemWithQuantity, List<CartToppingItemWithQuantity>> noToppingEntry;
    private BigDecimal totalProductItemNoToppingPrice;

    private Map<CartProductItemWithQuantity, List<CartToppingItemWithQuantity>> productsToToppings;

    private FullCartDiscountHandler underTest;

    @BeforeEach
    void setUp() {

    }

    @Test
    void whenCartHasNoProductItems_thenEmptyOptionalReturned() {
        assertThat(underTest.handle(new CartItemMap(Map.of())))
                .isEmpty();

    }

    @Test
    void whenOriginalPriceDoesNotMeetThreshold_thenEmptyOptionalReturned() {
    }

    @Test
    void whenOriginalPriceExceedsThresholdFromProductsOnly_thenPriceReducedByMultiplier() {

    }

    @Test
    void whenOriginalPriceMeetsThresholdOnlyWhenIncludingProductsAndToppings_thenPriceReducedByMultiplier() {
    }


    @Test
    void whenFinalPriceHasFractionalCents_thenItIsFloored() {
        //TODO
    }

}
