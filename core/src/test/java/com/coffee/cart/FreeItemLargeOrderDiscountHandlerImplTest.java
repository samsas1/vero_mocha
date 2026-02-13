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
    private CartProductItem productItemQty2FreeTopping;
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
        // Total product price = 2 x 5.3 = 10.6
        // Total topping price = 1 x 0 = 0
        // Total price = 10.6 + 0 = 10.6
        productItemQty2FreeTopping = Instancio.of(CartProductItem.class)
                .set(field("price"), BigDecimal.valueOf(5.3))
                .set(field("quantity"), 2)
                .create();
        toppingItemFreeTopping = Instancio.of(CartToppingItem.class)
                .set(field("price"), BigDecimal.ZERO)
                .set(field("quantity"), 1)
                .create();
        totalProductItemFreeToppingPrice = BigDecimal.valueOf(10.6);

        // Components for free product product item
        // Total product price = 1 x 0 = 0
        // Total topping price = 2 x 6 = 12
        // Total price = 0 + 12 = 12
        productItemFreeProduct = Instancio.of(CartProductItem.class)
                .set(field("price"), BigDecimal.ZERO)
                .set(field("quantity"), 1)
                .create();
        toppingItemFreeProduct = Instancio.of(CartToppingItem.class)
                .set(field("price"), BigDecimal.valueOf(6))
                .set(field("quantity"), 2)
                .create();
        totalProductItemFreeProductPrice = BigDecimal.valueOf(12);

        // Components for no topping product item
        // Total product price = 1 x 7 = 7
        // Total topping price = 0 x 0 = 0
        // Total price = 0 + 7 = 7
        productItemNoTopping = Instancio.of(CartProductItem.class)
                .set(field("price"), BigDecimal.valueOf(7))
                .set(field("quantity"), 1)
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
                new CartItem(productItemQty2FreeTopping, List.of(toppingItemFreeTopping)),
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
                                originalPrice.setScale(2),
                                finalPrice.setScale(2)
                        )
                );

    }

    @Test
    void whenProductItemThresholdExceeded_thenPriceReducedByCheapestItemAndItemReturned() {
        // Four entries (threshold exceeded)
        cartItems = List.of(
                new CartItem(productItemCheapest,
                        List.of(toppingItemCheapest1, toppingItemCheapest2, toppingItemCheapest3)),
                new CartItem(productItemQty2FreeTopping, List.of(toppingItemFreeTopping)),
                new CartItem(productItemFreeProduct, List.of(toppingItemFreeProduct)),
                new CartItem(productItemNoTopping, List.of())
        );
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
                                originalPrice.setScale(2),
                                finalPrice.setScale(2)
                        )
                );
    }

    @Test
    void whenProductItemThresholdExceededFromProductItemsOfTheSameProduct_thenReducePriceByItsPrice() {
        BigDecimal productPrice = BigDecimal.valueOf(1.1);
        Integer productQuantity = 10;
        // Total price is 11
        BigDecimal originalPrice = BigDecimal.valueOf(11).setScale(2);
        // Reduce price by one product price: 11 - 1.1 = 9.9
        BigDecimal finalPrice = BigDecimal.valueOf(9.9).setScale(2);
        CartProductItem cartProductItem = Instancio.of(CartProductItem.class)
                .set(field("price"), productPrice)
                .set(field("quantity"), productQuantity)
                .create();

        cartItems = List.of(new CartItem(cartProductItem, List.of()));

        assertThat(underTest.handle(new CartItemList(cartItems)).get())
                .isEqualTo(new ExternalDiscountResponse(
                                FREE_ITEM_FOR_LARGE_ORDER,
                                originalPrice,
                                finalPrice
                        )
                );
    }

    @Test
    void whenProductItemThresholdExceededFromProductItemsOfTheSameProduct_thenReducePriceByProductAndToppingPrice() {
        BigDecimal productPrice = BigDecimal.valueOf(1.1);
        Integer productQuantity = 10;

        BigDecimal toppingPrice = BigDecimal.valueOf(0.5);
        Integer toppingPerProductQuantity = 1;

        // Total price for 10 products with 1 topping each is
        // 10 x (1.1 + 1 x 0,5) = 16
        BigDecimal originalPrice = BigDecimal.valueOf(16).setScale(2);
        // Reduce price by one product price and one topping price: 16 - (1.1 + 0.5) = 14.4
        BigDecimal finalPrice = BigDecimal.valueOf(14.4).setScale(2);

        CartProductItem cartProductItem = Instancio.of(CartProductItem.class)
                .set(field("price"), productPrice)
                .set(field("quantity"), productQuantity)
                .create();

        CartToppingItem cartToppingItem = Instancio.of(CartToppingItem.class)
                .set(field("price"), toppingPrice)
                .set(field("quantity"), toppingPerProductQuantity)
                .create();

        cartItems = List.of(new CartItem(cartProductItem, List.of(cartToppingItem)));

        assertThat(underTest.handle(new CartItemList(cartItems)).get())
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
