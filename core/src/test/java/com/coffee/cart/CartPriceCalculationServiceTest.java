package com.coffee.cart;

import com.coffee.cart.entity.CartItem;
import com.coffee.cart.entity.CartItemList;
import com.coffee.cart.entity.CartProductItem;
import com.coffee.cart.entity.CartToppingItem;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

public class CartPriceCalculationServiceTest {


    @Test
    void whenOneProductItemInCartNoToppings_thenTotalProductItemPriceAndFullCartPriceIsIsEqualToProductPriceTimesProductItemQuantity() {
        BigDecimal cappuccinoPrice = BigDecimal.valueOf(3.0);
        Integer cappuccinoOrderQty = 2;

        BigDecimal totalProductsPrice = BigDecimal.valueOf(6).setScale(2);

        CartProductItem cartProductItem = createCartProductItem(cappuccinoPrice, cappuccinoOrderQty);

        CartItemList cartItemList = new CartItemList(
                List.of(new CartItem(cartProductItem, List.of()))
        );

        assertThat(CartPriceCalculationService.getTotalProductsPrice(cartItemList))
                .isEqualTo(totalProductsPrice);
        assertThat(CartPriceCalculationService.getTotalCartPrice(cartItemList))
                .isEqualTo(totalProductsPrice);
        assertThat(CartPriceCalculationService.getTotalToppingsPrice(cartItemList))
                .isEqualTo(BigDecimal.ZERO.setScale(2));
    }


    @Test
    void whenOneProductItemForTwoProducts_thenTotalProductPriceAndFullCartPriceIsEqualToProductPriceTimesProductItemQuantitySumBetweenProductsI() {
        BigDecimal cappuccinoPrice = BigDecimal.valueOf(3.0);
        Integer cappuccinoItemQty = 2;
        BigDecimal espressoPrice = BigDecimal.valueOf(2.0);
        Integer espressoItemQty = 1;
        // 2 cappuccinos with price 3 and 1 espresso with price 2 = 2 x 3 + 1 x 2 = 8
        BigDecimal totalProductsPrice = BigDecimal.valueOf(8).setScale(2);

        CartProductItem cappuccinoProductItem = createCartProductItem(cappuccinoPrice, cappuccinoItemQty);
        CartProductItem espressoProductItem = createCartProductItem(espressoPrice, espressoItemQty);

        CartItemList cartItemList = new CartItemList(
                List.of(new CartItem(cappuccinoProductItem, List.of()),
                        new CartItem(espressoProductItem, List.of())
                )
        );

        assertThat(CartPriceCalculationService.getTotalProductsPrice(cartItemList))
                .isEqualTo(totalProductsPrice);

        assertThat(CartPriceCalculationService.getTotalCartPrice(cartItemList))
                .isEqualTo(totalProductsPrice);

        assertThat(CartPriceCalculationService.getTotalToppingsPrice(cartItemList))
                .isEqualTo(BigDecimal.ZERO.setScale(2));
    }

    @Test
    void whenMultipleOrdersForRepeatedProductItems_thenTotalProductPriceAndTotalCartPriceIsEqualToProductPriceTimesProductItemQuantitySumBetweenProducts() {
        BigDecimal cappuccinoPrice = BigDecimal.valueOf(3.0);
        Integer cappuccinoOrderItem1Qty = 2;
        Integer cappuccinoOrderItem2Qty = 5;
        BigDecimal espressoPrice = BigDecimal.valueOf(2.0);
        Integer espressoOrderItemQty = 1;

        // 7 cappuccinos with price 3 and 1 espresso with price 2 = 7 x 3 + 1 x 2 = 23
        BigDecimal totalProductsPrice = BigDecimal.valueOf(23).setScale(2);

        CartProductItem cappuccinoProductItem1 = createCartProductItem(cappuccinoPrice, cappuccinoOrderItem1Qty);
        CartProductItem cappuccinoProductItem2 = createCartProductItem(cappuccinoPrice, cappuccinoOrderItem2Qty);
        CartProductItem espressoProductItem = createCartProductItem(espressoPrice, espressoOrderItemQty);

        CartItemList cartItemList = new CartItemList(
                List.of(new CartItem(cappuccinoProductItem1, List.of()),
                        new CartItem(cappuccinoProductItem2, List.of()),
                        new CartItem(espressoProductItem, List.of())
                )
        );

        assertThat(CartPriceCalculationService.getTotalProductsPrice(cartItemList))
                .isEqualTo(totalProductsPrice);

        assertThat(CartPriceCalculationService.getTotalCartPrice(cartItemList))
                .isEqualTo(totalProductsPrice);

        assertThat(CartPriceCalculationService.getTotalToppingsPrice(cartItemList))
                .isEqualTo(BigDecimal.ZERO.setScale(2));
    }

    @Test
    void whenOneProductItemInCartWithTopping_thenTotalToppingPriceIsEqualToProductQuantityTimesToppingQuantityTimesToppingPrice() {
        BigDecimal cappuccinoPrice = BigDecimal.valueOf(3.0);
        Integer cappuccinoOrderItemQty = 2;
        BigDecimal vanillaPrice = BigDecimal.valueOf(0.5);
        Integer vanillaPerProductItemQty = 3;

        // 2 cappuccinos with 3 vanilla toppings of price 0.5 each = 6 vanilla toppings of price 0.5 each = 3
        BigDecimal totalToppingsPrice = BigDecimal.valueOf(3).setScale(2);

        CartProductItem cappuccinoProductItem1 = createCartProductItem(cappuccinoPrice, cappuccinoOrderItemQty);
        CartToppingItem vanillaToppingItem = createCartToppingItem(vanillaPrice, vanillaPerProductItemQty);

        CartItemList cartItemList = new CartItemList(
                List.of(new CartItem(cappuccinoProductItem1, List.of(vanillaToppingItem))
                )
        );

        assertThat(CartPriceCalculationService.getTotalToppingsPrice(cartItemList))
                .isEqualTo(totalToppingsPrice);
    }


    @Test
    void whenMultipleProductItemsInCartWithToppings_thenTotalPricesCalculatedCorrectly() {
        BigDecimal cappuccinoPrice = BigDecimal.valueOf(3.0);
        BigDecimal espressoPrice = BigDecimal.valueOf(2.0);
        BigDecimal vanillaPrice = BigDecimal.valueOf(0.5);
        BigDecimal chocolatePrice = BigDecimal.valueOf(0.7);

        Integer cappuccinoOrderItem1Qty = 2;
        Integer vanillaPerProductOrderItem1Qty = 3;
        Integer chocolatePerCappuccinoProductOrderItem1Qty = 1;

        Integer cappuccinoOrderItem2Qty = 1;
        Integer chocolatePerCappuccinoProductOrderItem2Qty = 3;

        Integer espressoOrderItemQty = 4;
        Integer vanillaPerEspressoProductOrderItemQty = 3;


        // 2 cappuccinos with price 3 and 1 cappucino with price 3 = 2 x 3 + 1 x 3 = 9
        // Total cappuccino products price = 9
        // 4 espressos with price 2 = 4 x 2 = 8
        // Total espresso products price = 8
        // Total products price = 9 + 8 = 17
        BigDecimal totalProductsPrice = BigDecimal.valueOf(17).setScale(2);

        // 3 vanilla with price 0.5 on 2 cappuccino and 3 vanilla with price 0.5 on 4 espresso = 3 x 0.5 x 2 + 3 x 0.5 x 4 = 3 + 6 = 9
        // Total vanilla toppings price = 9
        // 1 chocolate with price 0.7 on 2 cappuccino and 3 chocolate with price 0.7 on 1 cappuccino = (1 x 0.7 x 2) + (3 x 0.7 x 1) = 3.5
        // Total chocolate toppings price = 3.5
        //Total toppings price = 9 + 3.5 = 12.5
        BigDecimal totalToppingsPrice = BigDecimal.valueOf(12.5).setScale(2);
        // Total cart price = 17 + 12.5 = 29.5
        BigDecimal totalCartPrice = BigDecimal.valueOf(29.5).setScale(2);


        CartProductItem cappuccinoProductItem1 = createCartProductItem(
                cappuccinoPrice,
                cappuccinoOrderItem1Qty
        );
        CartToppingItem vanillaToppingItemForCappuccinoProductItem1 = createCartToppingItem(
                vanillaPrice,
                vanillaPerProductOrderItem1Qty
        );
        CartToppingItem chocolateToppingItemForCappuccinoProductItem1 = createCartToppingItem(
                chocolatePrice,
                chocolatePerCappuccinoProductOrderItem1Qty
        );

        CartProductItem cappuccinoProductItem2 = createCartProductItem(
                cappuccinoPrice,
                cappuccinoOrderItem2Qty
        );

        CartToppingItem chocolateToppingItemForCappuccinoProductItem2 = createCartToppingItem(
                chocolatePrice,
                chocolatePerCappuccinoProductOrderItem2Qty
        );

        CartProductItem espressoProductItem = createCartProductItem(
                espressoPrice,
                espressoOrderItemQty
        );
        CartToppingItem vanillaToppingItemForEspressoProductItem = createCartToppingItem(
                vanillaPrice,
                vanillaPerEspressoProductOrderItemQty
        );


        CartItemList cartItemList = new CartItemList(
                List.of(new CartItem(
                                cappuccinoProductItem1,
                                List.of(vanillaToppingItemForCappuccinoProductItem1, chocolateToppingItemForCappuccinoProductItem1)
                        ),
                        new CartItem(
                                cappuccinoProductItem2,
                                List.of(chocolateToppingItemForCappuccinoProductItem2)
                        ),
                        new CartItem(
                                espressoProductItem,
                                List.of(vanillaToppingItemForEspressoProductItem)
                        )
                )
        );

        assertThat(CartPriceCalculationService.getTotalProductsPrice(cartItemList))
                .isEqualTo(totalProductsPrice);

        assertThat(CartPriceCalculationService.getTotalToppingsPrice(cartItemList))
                .isEqualTo(totalToppingsPrice);

        assertThat(CartPriceCalculationService.getTotalCartPrice(cartItemList))
                .isEqualTo(totalCartPrice);
    }

    private CartProductItem createCartProductItem(BigDecimal price, Integer quantity) {
        return Instancio.of(CartProductItem.class)
                .set(field("price"), price)
                .set(field("quantity"), quantity)
                .create();
    }

    private CartToppingItem createCartToppingItem(BigDecimal price, Integer quantity) {
        return Instancio.of(CartToppingItem.class)
                .set(field("price"), price)
                .set(field("quantity"), quantity)
                .create();
    }
}
