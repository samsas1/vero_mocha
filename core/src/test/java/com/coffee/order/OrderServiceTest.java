package com.coffee.order;

import com.coffee.cart.CartItemService;
import com.coffee.cart.DiscountService;
import com.coffee.publicapi.ExternalOrderPlacementResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderServiceTest {

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final OrderProductItemRepository orderProductItemRepository = mock(OrderProductItemRepository.class);
    private final OrderToppingItemRepository orderToppingItemRepository = mock(OrderToppingItemRepository.class);
    private final DiscountService discountService = mock(DiscountService.class);
    private final CartItemService cartItemService = mock(CartItemService.class);

    private OrderService underTest;

    @BeforeEach
    void setUp() {
        underTest = new OrderService(
                orderRepository,
                orderProductItemRepository,
                orderToppingItemRepository,
                discountService,
                cartItemService
        );
    }

    @Test
    void whenCreatingOrderWithAnEmptyCart_thenEmptyCartResponseReturned() {
        UUID userUid = UUID.randomUUID();
        when(cartItemService.isCartEmpty(userUid))
                .thenReturn(true);

        assertThat(underTest.placeOrder(userUid))
                .extracting(
                        ExternalOrderPlacementResponse::orderUid,
                        ExternalOrderPlacementResponse::originalPrice,
                        ExternalOrderPlacementResponse::finalPrice,
                        ExternalOrderPlacementResponse::placed,
                        ExternalOrderPlacementResponse::message)
                .containsExactly(
                        null,
                        null,
                        null,
                        false,
                        "No order created. Cart is empty."
                );

    }
}
