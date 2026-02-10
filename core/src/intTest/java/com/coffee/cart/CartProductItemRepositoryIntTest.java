package com.coffee.cart;

import com.coffee.cart.entity.CartEntity;
import com.coffee.cart.entity.CartProductItemEntity;
import com.coffee.item.ProductRepository;
import com.coffee.item.entity.ProductEntity;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class CartProductItemRepositoryIntTest {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartProductItemRepository cartProductItemRepository;

    private CartEntity cart;
    private ProductEntity product;

    @BeforeEach
    void setUp() {
        cart = Instancio.of(CartEntity.class)
                .set(field("sid"), null) // required for auto generation
                .create();
        product = Instancio.of(ProductEntity.class)
                .set(field("sid"), null) // required for auto generation
                .set(field("price"), BigDecimal.ONE) // required for positive value db constraint
                .create();
        cartRepository.save(cart);
        productRepository.save(product);
    }

    @Test
    void whenCartProductItemPersisted_thenItCanBeRetrieved() {
        // Truncated to millis to avoid precision loss when comparing with db value
        Instant instant = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        UUID uid = UUID.randomUUID();
        cartProductItemRepository.saveCartProduct(
                uid,
                cart.getUserUid(),
                product.getUid(),
                1,
                instant
        );

        CartProductItemEntity cartProductItemEntity = cartProductItemRepository.findByUid(uid).orElseThrow();

        assertThat(cartProductItemEntity)
                .isNotNull()
                .extracting(
                        CartProductItemEntity::getUid,
                        CartProductItemEntity::getCart,
                        CartProductItemEntity::getProduct,
                        CartProductItemEntity::getCreatedAt
                )
                .containsExactly(
                        uid,
                        cart,
                        product,
                        instant
                );
    }

    @Test
    void whenMultipleCartProductItemsForTheSameCart_thenAllCanBeRetrieved() {
        // TODO
    }

    @Test
    void whenMultipleCartProductItemsForTheSameProduct_thenAllCanBePersistedAndRetrieved() {
        // TODO
    }
}
