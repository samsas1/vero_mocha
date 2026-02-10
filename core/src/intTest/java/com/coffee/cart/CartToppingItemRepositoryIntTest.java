package com.coffee.cart;


import com.coffee.cart.batch.CartToppingItemBatchRepository.CartTopping;
import com.coffee.cart.entity.CartEntity;
import com.coffee.cart.entity.CartToppingItemEntity;
import com.coffee.item.ProductRepository;
import com.coffee.item.ToppingRepository;
import com.coffee.item.entity.ProductEntity;
import com.coffee.item.entity.ToppingEntity;
import jakarta.transaction.Transactional;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class CartToppingItemRepositoryIntTest {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ToppingRepository toppingRepository;

    @Autowired
    CartProductItemRepository cartProductItemRepository;

    @Autowired
    CartToppingItemRepository cartToppingItemRepository;


    private CartEntity cart;
    private ProductEntity product;
    private ToppingEntity topping;
    private UUID cartProductItemUid;
    private int quantityOfCartProductItem;
    private Instant instant;
    private UUID cartToppingItemUid;
    private int quantityOfCartToppingItem;

    @BeforeEach
    void setUp() {
        cart = Instancio.of(CartEntity.class)
                .set(field("sid"), null) // required for auto generation
                .create();
        product = Instancio.of(ProductEntity.class)
                .set(field("sid"), null) // required for auto generation
                .set(field("price"), BigDecimal.ONE) // required for positive value db constraint
                .create();
        topping = Instancio.of(ToppingEntity.class)
                .set(field("sid"), null) // required for auto generation
                .set(field("price"), BigDecimal.ONE) // required for positive value db constraint
                .create();

        cartRepository.save(cart);
        productRepository.save(product);
        toppingRepository.save(topping);

        cartProductItemUid = UUID.randomUUID();
        quantityOfCartProductItem = Instancio.create(int.class);
        // Truncated to millis to avoid precision loss when comparing with db value
        instant = Instant.now().truncatedTo(ChronoUnit.MILLIS);

        cartProductItemRepository.saveCartProduct(
                cartProductItemUid,
                cart.getUserUid(),
                product.getUid(),
                quantityOfCartProductItem,
                instant
        );

        cartToppingItemUid = UUID.randomUUID();
        quantityOfCartToppingItem = Instancio.create(int.class);

    }

    @Test
    void whenCartToppingItemIsPersisted_thenItCanBeRetrieved() {
        CartTopping cartTopping = new CartTopping(
                cartToppingItemUid,
                cartProductItemUid,
                topping.getUid(),
                quantityOfCartToppingItem,
                instant
        );
        cartToppingItemRepository.saveCartProduct(List.of(cartTopping));

        CartToppingItemEntity cartToppingItemEntity = cartToppingItemRepository.findByUid(cartToppingItemUid).orElseThrow();

        assertThat(cartToppingItemEntity)
                .isNotNull()
                .extracting(
                        CartToppingItemEntity::getUid,
                        o -> o.getCartProductItem().getUid(),
                        CartToppingItemEntity::getTopping,
                        CartToppingItemEntity::getQuantity,
                        CartToppingItemEntity::getCreatedAt
                )
                .containsExactly(
                        cartToppingItemUid,
                        cartProductItemUid,
                        topping,
                        quantityOfCartToppingItem,
                        instant
                );

    }

    @Test
    void whenMultipleCartToppingItemsWithTheSameProductItemButDifferentToppings_thenAllCanBePersistedAndRetrieved() {
        // TODO
    }

    @Test
    void whenMultipleCartToppingItemsWithTheSameProductItemAndSameToppings_thenPersistenceFails() {
        // TODO
    }

    @Test
    void whenMultipleCartToppingItemsWithDifferentProductItemButSameToppings_thenAllCanBePersistedAndRetrieved() {
        // TODO
    }

    @Test
    void whenCartToppingItemForNonExistingProductItem_thenPersistenceFails() {
        // TODO
    }

    @Test
    void whenCartToppingItemForNonExistingTopping_thenPersistenceFails() {
        // TODO
    }

    @Test
    void whenCartToppingItemQuantityBelowOne_thenPersistenceFails() {
        // TODO
    }
}

