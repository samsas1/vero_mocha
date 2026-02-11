package com.coffee.cart;


import com.coffee.cart.custom.query.batch.CartToppingItemBatchRepository.CartToppingItem;
import com.coffee.cart.entity.database.CartEntity;
import com.coffee.cart.entity.database.CartToppingItemEntity;
import com.coffee.item.ProductRepository;
import com.coffee.item.ToppingRepository;
import com.coffee.item.entity.database.ProductEntity;
import com.coffee.item.entity.database.ToppingEntity;
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
import static org.assertj.core.groups.Tuple.tuple;
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
    CartToppingItemRepository underTest;


    private CartEntity cart;
    private ProductEntity product;
    private ToppingEntity topping;
    private ToppingEntity anotherTopping;
    private UUID cartProductItemUid;
    private int quantityOfCartProductItem;
    private Instant instant;
    private UUID cartToppingItemUid;
    private int quantityOfCartToppingItem;
    private int quantityOfAnotherCartToppingItem;
    private CartToppingItem cartToppingItem;

    @BeforeEach
    void setUp() {
        // Set up cart
        cart = Instancio.of(CartEntity.class)
                .set(field("sid"), null) // required for auto generation
                .create();
        cartRepository.save(cart);

        // Set up product
        product = Instancio.of(ProductEntity.class)
                .set(field("sid"), null) // required for auto generation
                .set(field("price"), BigDecimal.ONE) // required for positive value db constraint
                .create();
        productRepository.save(product);

        // Set up toppings
        topping = Instancio.of(ToppingEntity.class)
                .set(field("sid"), null) // required for auto generation
                .set(field("price"), BigDecimal.ONE) // required for positive value db constraint
                .create();
        toppingRepository.save(topping);

        anotherTopping = Instancio.of(ToppingEntity.class)
                .set(field("sid"), null) // required for auto generation
                .set(field("price"), BigDecimal.ONE) // required for positive value db constraint
                .create();
        toppingRepository.save(anotherTopping);


        // Set up product item
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

        // Cart topping properties
        cartToppingItemUid = UUID.randomUUID();
        quantityOfCartToppingItem = Instancio.create(int.class);
        quantityOfAnotherCartToppingItem = Instancio.create(int.class);
        cartToppingItem = new CartToppingItem(
                cartToppingItemUid,
                cartProductItemUid,
                topping.getUid(),
                quantityOfCartToppingItem,
                instant
        );

    }

    @Test
    void whenCartToppingItemIsPersisted_thenItCanBeRetrieved() {
        underTest.saveCartProduct(List.of(cartToppingItem));

        CartToppingItemEntity cartToppingItemEntity = underTest.findByUid(cartToppingItemUid).orElseThrow();

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
        CartToppingItem anotherCartToppingItem = new CartToppingItem(
                UUID.randomUUID(),
                cartProductItemUid,
                anotherTopping.getUid(),
                quantityOfAnotherCartToppingItem,
                instant
        );

        underTest.saveCartProduct(List.of(cartToppingItem, anotherCartToppingItem));

        List<CartToppingItemEntity> cartToppingItemEntities = underTest.findAll();

        assertThat(cartToppingItemEntities)
                .extracting(
                        CartToppingItemEntity::getUid,
                        o -> o.getCartProductItem().getUid(),
                        CartToppingItemEntity::getTopping,
                        CartToppingItemEntity::getQuantity,
                        CartToppingItemEntity::getCreatedAt
                )
                .containsExactlyInAnyOrder(
                        tuple(
                                cartToppingItemUid,
                                cartProductItemUid,
                                topping,
                                quantityOfCartToppingItem,
                                instant
                        ),
                        tuple(
                                anotherCartToppingItem.uid(),
                                cartProductItemUid,
                                anotherTopping,
                                quantityOfAnotherCartToppingItem,
                                instant
                        )
                );
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

