package com.coffee.cart.cart;

import com.coffee.cart.FullCartDiscountHandlerImpl;
import com.coffee.cart.entity.CartItemList;
import com.coffee.cart.entity.CartProductItem;
import com.coffee.cart.entity.CartToppingItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.assertj.core.api.Assertions.assertThat;

public class FullCartDiscountHandlerImplTest {
    private final BigDecimal discountMultiplier = BigDecimal.valueOf(0.75);
    private final BigDecimal discountThreshold = BigDecimal.valueOf(12);

    // Cheapest product item
    private CartProductItem productItemCheapest;
    private CartToppingItem toppingItemCheapest1;
    private CartToppingItem toppingItemCheapest2;
    private CartToppingItem toppingItemCheapest3;
    private Entry<CartProductItem, List<CartToppingItem>> cheapestEntry;
    private BigDecimal totalProductItemCheapestPrice;

    // Product item with free topping
    private CartProductItem productItemFreeTopping;
    private CartToppingItem toppingItemFreeTopping;
    private Entry<CartProductItem, List<CartToppingItem>> freeToppingEntry;
    private BigDecimal totalProductItemFreeToppingPrice;


    // Product item with free product
    private CartProductItem productItemFreeProduct;
    private CartToppingItem toppingItemFreeProduct;
    private Entry<CartProductItem, List<CartToppingItem>> freeProductEntry;
    private BigDecimal totalProductItemFreeProductPrice;

    // Product item with no toppings
    private CartProductItem productItemNoTopping;
    private Entry<CartProductItem, List<CartToppingItem>> noToppingEntry;
    private BigDecimal totalProductItemNoToppingPrice;

    private Map<CartProductItem, List<CartToppingItem>> productsToToppings;

    private FullCartDiscountHandlerImpl underTest;

    @BeforeEach
    void setUp() {

    }

    @Test
    void whenCartHasNoProductItems_thenEmptyOptionalReturned() {
        assertThat(underTest.handle(new CartItemList(List.of())))
                .isEmpty();

    }

    @Test
    void whenOriginalPriceDoesNotMeetThreshold_thenEmptyOptionalReturned() {
        //TODO
    }

    @Test
    void whenOriginalPriceExceedsThresholdFromProductsOnly_thenPriceReducedByMultiplier() {
        //TODO

    }

    @Test
    void whenOriginalPriceMeetsThresholdOnlyWhenIncludingProductsAndToppings_thenPriceReducedByMultiplier() {
        //TODO
    }


    @Test
    void whenFinalPriceHasFractionalCents_thenItIsFloored() {
        //TODO
    }

}
