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

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

public class FullCartDiscountHandlerImplTest {

    private FullCartDiscountHandlerImpl underTest;

    @BeforeEach
    void setUp() {

        underTest = new FullCartDiscountHandlerImpl();
    }

    @Test
    void whenCartHasNoProductItems_thenEmptyOptionalReturned() {
        assertThat(underTest.handle(new CartItemList(List.of())))
                .isEmpty();
    }

    @Test
    void whenOriginalPriceDoesNotMeetThresholdNoToppings_thenEmptyOptionalReturned() {
        // First product total = 1 x 1 = 1
        BigDecimal productPrice = BigDecimal.valueOf(1);
        int productQuantity = 1;
        // Second product total = 2 x 2 = 4
        BigDecimal otherProductPrice = BigDecimal.valueOf(2);
        int otherProductQuantity = 2;
        // Total price is 5 which does not meet the threshold of 12 for discount to be applied
        CartProductItem cartProductItem = Instancio.of(CartProductItem.class)
                .set(field("price"), productPrice)
                .set(field("quantity"), productQuantity)
                .create();

        CartProductItem otherCartProductItem = Instancio.of(CartProductItem.class)
                .set(field("price"), otherProductPrice)
                .set(field("quantity"), otherProductQuantity)
                .create();

        CartItemList cartItemList = new CartItemList(
                List.of(
                        new CartItem(cartProductItem, List.of()),
                        new CartItem(otherCartProductItem, List.of())
                ));

        assertThat(underTest.handle(cartItemList))
                .isEmpty();
    }

    @Test
    void whenOriginalPriceDoesNotMeetThresholdWithToppings_thenEmptyOptionalReturned() {
        // Product total = 1 x 1 = 1
        BigDecimal productPrice = BigDecimal.valueOf(1);
        int productQuantity = 1;
        // Topping total = 2 x 3 = 6
        BigDecimal toppingPrice = BigDecimal.valueOf(2);
        int toppingQuantity = 3;
        // Total price is 6 which does not meet the threshold of 12 for discount to be applied
        CartProductItem cartProductItem = Instancio.of(CartProductItem.class)
                .set(field("price"), productPrice)
                .set(field("quantity"), productQuantity)
                .create();

        CartToppingItem cartToppingItem = Instancio.of(CartToppingItem.class)
                .set(field("price"), toppingPrice)
                .set(field("quantity"), toppingQuantity)
                .create();

        CartItemList cartItemList = new CartItemList(
                List.of(
                        new CartItem(cartProductItem, List.of(cartToppingItem))
                ));

        assertThat(underTest.handle(cartItemList))
                .isEmpty();
    }

    @Test
    void whenOriginalPriceMeetsThreshold_thenPriceReducedByMultiplier() {
        // Product total = 1 x 13 = 13
        BigDecimal productPrice = BigDecimal.valueOf(13);
        int productQuantity = 1;
        // Total price is 13 which meets the threshold of 12 for discount to be applied
        // Discounted price is 13 x 0.75 = 9.75
        BigDecimal originalPrice = BigDecimal.valueOf(13);
        BigDecimal discountedPrice = BigDecimal.valueOf(9.75);

        CartProductItem cartProductItem = Instancio.of(CartProductItem.class)
                .set(field("price"), productPrice)
                .set(field("quantity"), productQuantity)
                .create();

        CartItemList cartItemList = new CartItemList(
                List.of(
                        new CartItem(cartProductItem, List.of())
                ));

        assertThat(underTest.handle(cartItemList).orElseThrow())
                .extracting(
                        ExternalDiscountResponse::originalPrice,
                        ExternalDiscountResponse::finalPrice)
                .containsExactly(
                        originalPrice.setScale(2),
                        discountedPrice.setScale(2)
                );

    }

    @Test
    void whenOriginalPriceExceedsThresholdFromProducts_thenPriceReducedByMultiplier() {
        // First product total = 1 x 5 = 5
        BigDecimal productPrice = BigDecimal.valueOf(5);
        int productQuantity = 1;
        // Second product total = 2 x 4 = 8
        BigDecimal otherProductPrice = BigDecimal.valueOf(4);
        int otherProductQuantity = 2;
        // Total price is 13 which exceeds the threshold of 12 for discount to be applied
        // Discounted price is 13 x 0.75 = 9.75
        BigDecimal originalPrice = BigDecimal.valueOf(13);
        BigDecimal discountedPrice = BigDecimal.valueOf(9.75);

        CartProductItem cartProductItem = Instancio.of(CartProductItem.class)
                .set(field("price"), productPrice)
                .set(field("quantity"), productQuantity)
                .create();

        CartProductItem otherCartProductItem = Instancio.of(CartProductItem.class)
                .set(field("price"), otherProductPrice)
                .set(field("quantity"), otherProductQuantity)
                .create();

        CartItemList cartItemList = new CartItemList(
                List.of(
                        new CartItem(cartProductItem, List.of()),
                        new CartItem(otherCartProductItem, List.of())
                ));

        assertThat(underTest.handle(cartItemList).orElseThrow())
                .extracting(
                        ExternalDiscountResponse::originalPrice,
                        ExternalDiscountResponse::finalPrice)
                .containsExactly(
                        originalPrice.setScale(2),
                        discountedPrice.setScale(2)
                );

    }

    @Test
    void whenOriginalPriceExceedsThresholdOnlyWhenIncludingProductAndTopping_thenPriceReducedByMultiplier() {
        // Product total = 1 x 2 = 2
        BigDecimal productPrice = BigDecimal.valueOf(1);
        int productQuantity = 2;
        // Topping total = 4 x 10 = 40
        BigDecimal toppingPrice = BigDecimal.valueOf(2);
        int toppingQuantity = 10;
        // Total price is 42 which exceeds the threshold of 12 for discount to be applied
        // Discounted price is 42 x 0.75 = 31.5
        BigDecimal originalPrice = BigDecimal.valueOf(42);
        BigDecimal discountedPrice = BigDecimal.valueOf(31.5);

        CartProductItem cartProductItem = Instancio.of(CartProductItem.class)
                .set(field("price"), productPrice)
                .set(field("quantity"), productQuantity)
                .create();

        CartToppingItem cartToppingItem = Instancio.of(CartToppingItem.class)
                .set(field("price"), toppingPrice)
                .set(field("quantity"), toppingQuantity)
                .create();

        CartItemList cartItemList = new CartItemList(
                List.of(
                        new CartItem(cartProductItem, List.of(cartToppingItem))
                ));

        assertThat(underTest.handle(cartItemList).orElseThrow())
                .extracting(
                        ExternalDiscountResponse::originalPrice,
                        ExternalDiscountResponse::finalPrice)
                .containsExactly(
                        originalPrice.setScale(2),
                        discountedPrice.setScale(2)
                );
    }

    @Test
    void whenFinalPriceHasFractionalCents_thenItIsFloored() {
        CartProductItem cartProductItem = Instancio.of(CartProductItem.class)
                .set(field("price"), BigDecimal.valueOf(13.14))
                .set(field("quantity"), 1)
                .create();
        CartItemList cartItemList = new CartItemList(
                List.of(
                        new CartItem(cartProductItem, List.of())
                ));

        ExternalDiscountResponse externalDiscountResponse = underTest.handle(cartItemList).orElseThrow();

        assertThat(externalDiscountResponse.finalPrice()).isEqualByComparingTo(BigDecimal.valueOf(9.85));
    }

}
