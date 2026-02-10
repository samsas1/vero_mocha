package com.coffee.cart;

import com.coffee.cart.batch.CartToppingItemBatchRepository.CartTopping;
import com.coffee.publicapi.ExternalCartItemRequest;
import com.coffee.publicapi.ExternalToppingItemRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CartItemServiceTest {

    private final CartToppingItemRepository cartToppingItemRepository = mock(CartToppingItemRepository.class);
    private final CartProductItemRepository cartProductItemRepository = mock(CartProductItemRepository.class);

    private CartItemService cartItemService;

    @BeforeEach
    void setUp() {
        cartItemService = new CartItemService(cartProductItemRepository, cartToppingItemRepository);
    }

    @Test
    void whenMultipleToppingsWithSameUid_thenQuantitiesAreSummed() {
        var userUid = UUID.randomUUID();
        var productUid = UUID.randomUUID();
        var toppingUid = UUID.randomUUID();
        var otherToppingUid = UUID.randomUUID();
        int toppingQuantity1 = 2;
        int toppingQuantity2 = 3;
        int totalToppingQuantity = toppingQuantity1 + toppingQuantity2;
        int otherToppingQuantity = 4;


        var cartItemRequest = new ExternalCartItemRequest(
                productUid,
                1,
                List.of(
                        new ExternalToppingItemRequest(toppingUid, toppingQuantity1),
                        new ExternalToppingItemRequest(toppingUid, toppingQuantity2),
                        new ExternalToppingItemRequest(otherToppingUid, otherToppingQuantity)

                )
        );

        cartItemService.addItemToCart(userUid, cartItemRequest);

        ArgumentCaptor<List<CartTopping>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(cartToppingItemRepository).saveCartProduct(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue())
                .extracting(
                        CartTopping::toppingUid,
                        CartTopping::quantity
                )
                .containsExactlyInAnyOrder(
                        tuple(toppingUid, totalToppingQuantity),
                        tuple(otherToppingUid, otherToppingQuantity)
                );
    }
}
