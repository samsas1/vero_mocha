package com.coffee.cart;

import com.coffee.cart.entity.CartItem;
import com.coffee.cart.entity.CartItemList;
import com.coffee.cart.entity.CartProductItem;
import com.coffee.cart.entity.CartToppingItem;
import com.coffee.publicapi.ExternalDiscountResponse;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.coffee.publicapi.ExternalDiscountType.FREE_ITEM_FOR_LARGE_ORDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

public class FreeItemLargeOrderDiscountHandlerImplTest {

    // Cheapest product item
    private CartProductItem productItemCheapest;
    private CartToppingItem toppingItemCheapest1;
    private CartToppingItem toppingItemCheapest2;
    private CartToppingItem toppingItemCheapest3;
    private BigDecimal totalProductItemCheapestPrice;

    // Product item with free topping
    private CartProductItem productItemFreeTopping;
    private CartToppingItem toppingItemFreeTopping;
    private BigDecimal totalProductItemFreeToppingPrice;


    // Product item with free product
    private CartProductItem productItemFreeProduct;
    private CartToppingItem toppingItemFreeProduct;
    private BigDecimal totalProductItemFreeProductPrice;

    // Product item with no toppings
    private CartProductItem productItemNoTopping;
    private BigDecimal totalProductItemNoToppingPrice;

    private List<CartItem> cartItems;


    private FreeItemLargeOrderDiscountHandlerImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = new FreeItemLargeOrderDiscountHandlerImpl();

        // Components for cheapest product and topping
        // Total product price = 1 x 1.1 = 1.1
        // Total topping price = 1 x 1.2 + 1 x 1.3 + 1 x 1.4 = 3.9
        // Total price = 1.1 + 3.9 = 5
        productItemCheapest = Instancio.of(CartProductItem.class)
                .set(field("price"), BigDecimal.valueOf(1.1))
                .set(field("quantity"), 1)
                .create();
        toppingItemCheapest1 = Instancio.of(CartToppingItem.class)
                .set(field("price"), BigDecimal.valueOf(1.2))
                .set(field("quantity"), 1)
                .create();
        toppingItemCheapest2 = Instancio.of(CartToppingItem.class)
                .set(field("price"), BigDecimal.valueOf(1.3))
                .set(field("quantity"), 1)
                .create();
        toppingItemCheapest3 = Instancio.of(CartToppingItem.class)
                .set(field("price"), BigDecimal.valueOf(1.4))
                .set(field("quantity"), 1)
                .create();
        totalProductItemCheapestPrice = BigDecimal.valueOf(5);

        // Components for free topping product item
        // Total product price = 2 x 3.3 = 6.6
        // Total topping price = 1 x 0 = 0
        // Total price = 6.6 + 0 = 6.6
        productItemFreeTopping = Instancio.of(CartProductItem.class)
                .set(field("price"), BigDecimal.valueOf(3.3))
                .set(field("quantity"), 2)
                .create();
        toppingItemFreeTopping = Instancio.of(CartToppingItem.class)
                .set(field("price"), BigDecimal.ZERO)
                .set(field("quantity"), 1)
                .create();
        totalProductItemFreeToppingPrice = BigDecimal.valueOf(6.6);

        // Components for free product product item
        // Total product price = 1 x 0 = 0
        // Total topping price = 2 x 4 = 8
        // Total price = 0 + 8 = 8
        productItemFreeProduct = Instancio.of(CartProductItem.class)
                .set(field("price"), BigDecimal.ZERO)
                .set(field("quantity"), 1)
                .create();
        toppingItemFreeProduct = Instancio.of(CartToppingItem.class)
                .set(field("price"), BigDecimal.valueOf(4))
                .set(field("quantity"), 2)
                .create();
        totalProductItemFreeProductPrice = BigDecimal.valueOf(8);

        // Components for no topping product item
        // Total product price = 7 x 1 = 7
        // Total topping price = 0 x 0 = 0
        // Total price = 0 + 7 = 7
        productItemNoTopping = Instancio.of(CartProductItem.class)
                .set(field("price"), BigDecimal.ONE)
                .set(field("quantity"), 7)
                .create();
        totalProductItemNoToppingPrice = BigDecimal.valueOf(7);
    }

    @Test
    void whenNoProductItems_thenEmptyOptionalReturned() {
        assertThat(underTest.handle(new CartItemList(List.of())))
                .isEmpty();
    }

    @Test
    void whenProductItemsDoNotReachThresholdButToppingsDo_thenEmptyOptionalReturned() {
        // Only cheapest entry
        cartItems = List.of(
                new CartItem(productItemCheapest,
                        List.of(toppingItemCheapest1, toppingItemCheapest2, toppingItemCheapest3)));
        CartItemList cartItemList = new CartItemList(cartItems);
        assertThat(underTest.handle(cartItemList)).isEmpty();
    }

    @Test
    void whenProductItemThresholdReached_thenPriceReducedByCheapestItemAndItemReturned() {
        // Three entries (threshold met)
        cartItems = List.of(
                new CartItem(productItemCheapest,
                        List.of(toppingItemCheapest1, toppingItemCheapest2, toppingItemCheapest3)),
                new CartItem(productItemFreeTopping, List.of(toppingItemFreeTopping)),
                new CartItem(productItemFreeProduct, List.of(toppingItemFreeProduct)));
        // Original price is total price for all three entries
        BigDecimal originalPrice = totalProductItemCheapestPrice
                .add(totalProductItemFreeToppingPrice)
                .add(totalProductItemFreeProductPrice);

        // Discounted price does not include the cheapest product
        BigDecimal finalPrice = totalProductItemFreeToppingPrice
                .add(totalProductItemFreeProductPrice);

        CartItemList cartItemList = new CartItemList(cartItems);

        assertThat(underTest.handle(cartItemList).get())
                .isEqualTo(
                        new ExternalDiscountResponse(
                                FREE_ITEM_FOR_LARGE_ORDER,
                                originalPrice,
                                finalPrice
                        )
                );

    }

    @Test
    void whenProductItemThresholdExceeded_thenPriceReducedByCheapestItemAndItemReturned() {
        // Four entries (threshold exceeded)
        cartItems = List.of(
                new CartItem(productItemCheapest,
                        List.of(toppingItemCheapest1, toppingItemCheapest2, toppingItemCheapest3)),
                new CartItem(productItemFreeTopping, List.of(toppingItemFreeTopping)),
                new CartItem(productItemFreeProduct, List.of(toppingItemFreeProduct)),
                new CartItem(productItemNoTopping, List.of()));
        // Original price is total price for all three entries
        BigDecimal originalPrice = totalProductItemCheapestPrice
                .add(totalProductItemFreeToppingPrice)
                .add(totalProductItemFreeProductPrice)
                .add(totalProductItemNoToppingPrice);

        // Discounted price does not include the cheapest product
        BigDecimal finalPrice = totalProductItemFreeToppingPrice
                .add(totalProductItemFreeProductPrice)
                .add(totalProductItemNoToppingPrice);

        CartItemList cartItemList = new CartItemList(cartItems);

        assertThat(underTest.handle(cartItemList).get())
                .isEqualTo(new ExternalDiscountResponse(
                                FREE_ITEM_FOR_LARGE_ORDER,
                                originalPrice,
                                finalPrice
                        )
                );
    }

    @Test
    void whenProductItemThresholdReachedAndThereAreMultipleCheapestItems_thenPriceReducedByCheapestItem() {
        //TODO
    }


}
