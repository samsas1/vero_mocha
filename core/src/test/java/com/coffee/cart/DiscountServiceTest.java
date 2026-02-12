package com.coffee.cart;

import com.coffee.cart.entity.CartItemList;
import com.coffee.publicapi.ExternalDiscountResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.coffee.publicapi.ExternalDiscountType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DiscountServiceTest {

    private final CartItemService cartItemService = mock(CartItemService.class);
    private final DiscountHandler fullCartDiscountHandler = mock(DiscountHandler.class);
    private final DiscountHandler manyProductDiscountHandler = mock(DiscountHandler.class);
    private final List<DiscountHandler> discountHandlers = List.of(fullCartDiscountHandler, manyProductDiscountHandler);
    private final CartItemList cartItemList = mock(CartItemList.class);
    private UUID userUid;

    private DiscountService underTest;

    @BeforeEach
    void setUp() {
        userUid = UUID.randomUUID();
        underTest = new DiscountService(cartItemService, discountHandlers);
    }

    @Test
    void whenNoDiscounts_ThenReturnNoDiscountResponse() {
        BigDecimal totalCartPrice = BigDecimal.valueOf(10);

        when(cartItemService.getCartItemList(userUid))
                .thenReturn(cartItemList);
        when(cartItemList.getTotalOriginalPrice())
                .thenReturn(totalCartPrice);
        when(fullCartDiscountHandler.handle(cartItemList))
                .thenReturn(Optional.empty());
        when(manyProductDiscountHandler.handle(cartItemList))
                .thenReturn(Optional.empty());

        assertThat(underTest.getCartDiscount(userUid))
                .extracting(
                        ExternalDiscountResponse::discountType,
                        ExternalDiscountResponse::originalPrice,
                        ExternalDiscountResponse::finalPrice
                )
                .containsExactly(
                        NO_DISCOUNT,
                        totalCartPrice,
                        totalCartPrice
                );

    }

    @Test
    void whenOneDiscountReturned_thenReturnItsAmount() {
        BigDecimal totalCartPrice = BigDecimal.valueOf(20);
        BigDecimal reducedCartPrice = BigDecimal.valueOf(15);
        ExternalDiscountResponse discountResponse = new ExternalDiscountResponse(
                FULL_CART,
                totalCartPrice,
                reducedCartPrice
        );

        when(cartItemService.getCartItemList(userUid))
                .thenReturn(cartItemList);
        when(cartItemList.getTotalOriginalPrice())
                .thenReturn(totalCartPrice);
        when(fullCartDiscountHandler.handle(cartItemList))
                .thenReturn(Optional.of(discountResponse));
        when(manyProductDiscountHandler.handle(cartItemList))
                .thenReturn(Optional.empty());

        assertThat(underTest.getCartDiscount(userUid))
                .extracting(
                        ExternalDiscountResponse::discountType,
                        ExternalDiscountResponse::originalPrice,
                        ExternalDiscountResponse::finalPrice
                )
                .containsExactly(
                        FULL_CART,
                        totalCartPrice,
                        reducedCartPrice
                );
    }

    @Test
    void whenMultipleDiscounts_ThenReturnSmallestDiscount() {
        BigDecimal totalCartPrice = BigDecimal.valueOf(20);
        BigDecimal reducedCartPrice1 = BigDecimal.valueOf(15);
        BigDecimal reducedCartPrice2 = BigDecimal.valueOf(10);

        ExternalDiscountResponse discountResponse1 = new ExternalDiscountResponse(
                FULL_CART,
                totalCartPrice,
                reducedCartPrice1
        );
        ExternalDiscountResponse discountResponse2 = new ExternalDiscountResponse(
                FREE_ITEM_FOR_LARGE_ORDER,
                totalCartPrice,
                reducedCartPrice2
        );

        when(cartItemService.getCartItemList(userUid))
                .thenReturn(cartItemList);
        when(cartItemList.getTotalOriginalPrice())
                .thenReturn(totalCartPrice);
        when(fullCartDiscountHandler.handle(cartItemList))
                .thenReturn(Optional.of(discountResponse1));
        when(manyProductDiscountHandler.handle(cartItemList))
                .thenReturn(Optional.of(discountResponse2));

        assertThat(underTest.getCartDiscount(userUid))
                .extracting(
                        ExternalDiscountResponse::discountType,
                        ExternalDiscountResponse::originalPrice,
                        ExternalDiscountResponse::finalPrice
                )
                .containsExactly(
                        FREE_ITEM_FOR_LARGE_ORDER,
                        totalCartPrice,
                        reducedCartPrice2
                );

    }

}
