package com.coffee.cart.order;

import com.coffee.order.FreeItemLargeOrderDiscountHandler;
import com.coffee.order.entity.CartItemMap;
import com.coffee.order.entity.CartProductItemWithQuantity;
import com.coffee.order.entity.CartToppingItemWithQuantity;
import com.coffee.publicapi.ExternalDiscountResponse;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.coffee.publicapi.ExternalDiscountType.FREE_ITEM_FOR_LARGE_ORDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

public class FreeItemLargeOrderDiscountHandlerTest {


    private final int largeOrderProductCountThreshold = 3;

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


    private FreeItemLargeOrderDiscountHandler underTest;

    @BeforeEach
    void setUp() {
        underTest = new FreeItemLargeOrderDiscountHandler();

        // Components for cheapest product and topping
        // Total product price = 1 x 1.1 = 1.1
        // Total topping price = 1 x 1.2 + 1 x 1.3 + 1 x 1.4 = 3.9
        // Total price = 1.1 + 3.9 = 5
        productItemCheapest = Instancio.of(CartProductItemWithQuantity.class)
                .set(field("price"), BigDecimal.valueOf(1.1))
                .set(field("quantity"), 1)
                .create();
        toppingItemCheapest1 = Instancio.of(CartToppingItemWithQuantity.class)
                .set(field("price"), BigDecimal.valueOf(1.2))
                .set(field("quantity"), 1)
                .create();
        toppingItemCheapest2 = Instancio.of(CartToppingItemWithQuantity.class)
                .set(field("price"), BigDecimal.valueOf(1.3))
                .set(field("quantity"), 1)
                .create();
        toppingItemCheapest3 = Instancio.of(CartToppingItemWithQuantity.class)
                .set(field("price"), BigDecimal.valueOf(1.4))
                .set(field("quantity"), 1)
                .create();
        cheapestEntry = Map.entry(productItemCheapest, List.of(toppingItemCheapest1, toppingItemCheapest2, toppingItemCheapest3));
        totalProductItemCheapestPrice = BigDecimal.valueOf(5);

        // Components for free topping product item
        // Total product price = 2 x 3.3 = 6.6
        // Total topping price = 1 x 0 = 0
        // Total price = 6.6 + 0 = 6.6
        productItemFreeTopping = Instancio.of(CartProductItemWithQuantity.class)
                .set(field("price"), BigDecimal.valueOf(3.3))
                .set(field("quantity"), 2)
                .create();
        toppingItemFreeTopping = Instancio.of(CartToppingItemWithQuantity.class)
                .set(field("price"), BigDecimal.ZERO)
                .set(field("quantity"), 1)
                .create();
        freeToppingEntry = Map.entry(productItemFreeTopping, List.of(toppingItemFreeTopping));
        totalProductItemFreeToppingPrice = BigDecimal.valueOf(6.6);

        // Components for free product product item
        // Total product price = 1 x 0 = 0
        // Total topping price = 2 x 4 = 8
        // Total price = 0 + 8 = 8
        productItemFreeProduct = Instancio.of(CartProductItemWithQuantity.class)
                .set(field("price"), BigDecimal.ZERO)
                .set(field("quantity"), 1)
                .create();
        toppingItemFreeProduct = Instancio.of(CartToppingItemWithQuantity.class)
                .set(field("price"), BigDecimal.valueOf(4))
                .set(field("quantity"), 2)
                .create();
        freeProductEntry = Map.entry(productItemFreeProduct, List.of(toppingItemFreeProduct));
        totalProductItemFreeProductPrice = BigDecimal.valueOf(8);

        // Components for no topping product item
        // Total product price = 7 x 1 = 7
        // Total topping price = 0 x 0 = 0
        // Total price = 0 + 7 = 7
        productItemNoTopping = Instancio.of(CartProductItemWithQuantity.class)
                .set(field("price"), BigDecimal.ONE)
                .set(field("quantity"), 7)
                .create();
        noToppingEntry = Map.entry(productItemNoTopping, List.of());
        totalProductItemNoToppingPrice = BigDecimal.valueOf(7);

        productsToToppings = new HashMap<>();

    }


    @Test
    void whenNoProductItems_thenEmptyOptionalReturned() {
        assertThat(underTest.handle(new CartItemMap(Map.of())))
                .isEmpty();

    }

    @Test
    void whenProductItemsDoNotReachThreshold_thenEmptyOptionalReturned() {
        // Only cheapest entry
        productsToToppings.put(cheapestEntry.getKey(), cheapestEntry.getValue());
        CartItemMap cartItemMap = new CartItemMap(productsToToppings);

        assertThat(underTest.handle(cartItemMap)).isEmpty();
    }

    @Test
    void whenProductItemThresholdReached_thenPriceReducedByCheapestItemAndItemReturned() {
        // Three entries (threshold met)
        productsToToppings.put(cheapestEntry.getKey(), cheapestEntry.getValue());
        productsToToppings.put(freeToppingEntry.getKey(), freeToppingEntry.getValue());
        productsToToppings.put(freeProductEntry.getKey(), freeProductEntry.getValue());
        // Original price is total price for all three entries
        BigDecimal originalPrice = totalProductItemCheapestPrice
                .add(totalProductItemFreeToppingPrice)
                .add(totalProductItemFreeProductPrice);

        // Discounted price does not include the cheapest product
        BigDecimal finalPrice = totalProductItemFreeToppingPrice
                .add(totalProductItemFreeProductPrice);

        CartItemMap cartItemMap = new CartItemMap(productsToToppings);

        assertThat(underTest.handle(cartItemMap).get())
                .isEqualTo(new ExternalDiscountResponse(
                                FREE_ITEM_FOR_LARGE_ORDER,
                                originalPrice,
                                finalPrice
                        )
                );

    }

    @Test
    void whenProductItemThresholdExceeded_thenPriceReducedByCheapestItemAndItemReturned() {
        // Three entries (threshold exceeded)
        productsToToppings.put(cheapestEntry.getKey(), cheapestEntry.getValue());
        productsToToppings.put(freeToppingEntry.getKey(), freeToppingEntry.getValue());
        productsToToppings.put(freeProductEntry.getKey(), freeProductEntry.getValue());
        productsToToppings.put(noToppingEntry.getKey(), noToppingEntry.getValue());
        // Original price is total price for all three entries
        BigDecimal originalPrice = totalProductItemCheapestPrice
                .add(totalProductItemFreeToppingPrice)
                .add(totalProductItemFreeProductPrice)
                .add(totalProductItemNoToppingPrice);

        // Discounted price does not include the cheapest product
        BigDecimal finalPrice = totalProductItemFreeToppingPrice
                .add(totalProductItemFreeProductPrice)
                .add(totalProductItemNoToppingPrice);

        CartItemMap cartItemMap = new CartItemMap(productsToToppings);

        assertThat(underTest.handle(cartItemMap).get())
                .isEqualTo(new ExternalDiscountResponse(
                                FREE_ITEM_FOR_LARGE_ORDER,
                                originalPrice,
                                finalPrice
                        )
                );
    }

    @Test
    void whenProductItemThresholdReachedAndThereAreMultipleCheapestItems_thenPriceReducedByCheapestItemAndItemReturnedDeterministically() {
        //TODO
    }


}
