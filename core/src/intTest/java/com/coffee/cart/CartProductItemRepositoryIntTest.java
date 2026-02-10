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
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
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
    CartProductItemRepository underTest;

    private CartEntity cart;
    private ProductEntity product;
    private ProductEntity anotherProduct;
    private int quantityOfCartProductItem;
    private int quantityOfAnotherCartProductItem;
    private Instant instant;

    @BeforeEach
    void setUp() {
        // Set up cart
        cart = Instancio.of(CartEntity.class)
                .set(field("sid"), null) // required for auto generation
                .create();
        cartRepository.save(cart);

        // Set up products
        product = Instancio.of(ProductEntity.class)
                .set(field("sid"), null) // required for auto generation
                .set(field("price"), BigDecimal.ONE) // required for positive value db constraint
                .create();
        productRepository.save(product);

        anotherProduct = Instancio.of(ProductEntity.class)
                .set(field("sid"), null) // required for auto generation
                .set(field("price"), BigDecimal.valueOf(2)) // required for positive value db constraint
                .create();
        productRepository.save(anotherProduct);

        // Product item properties
        quantityOfCartProductItem = Instancio.create(int.class);
        quantityOfAnotherCartProductItem = Instancio.create(int.class);
        // Truncated to millis to avoid precision loss when comparing with db value
        instant = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    }

    @Test
    void whenCartProductItemPersisted_thenItCanBeRetrieved() {
        UUID uid = saveCartProductItem(cart, product, quantityOfCartProductItem, instant);
        CartProductItemEntity cartProductItemEntity = underTest.findByUid(uid).orElseThrow();

        assertThat(cartProductItemEntity)
                .isNotNull()
                .extracting(
                        CartProductItemEntity::getUid,
                        CartProductItemEntity::getCart,
                        CartProductItemEntity::getProduct,
                        CartProductItemEntity::getQuantity,
                        CartProductItemEntity::getCreatedAt
                )
                .containsExactly(
                        uid,
                        cart,
                        product,
                        quantityOfCartProductItem,
                        instant
                );
    }

    @Test
    void whenMultipleCartProductItemsWithTheSameProductForTheSameCart_thenAllCanBeRetrieved() {
        // First cart product item
        UUID uid = saveCartProductItem(cart, product, quantityOfCartProductItem, instant);
        // Second cart product item for the same cart and same product
        UUID secondUid = saveCartProductItem(cart, product, quantityOfCartProductItem, instant);

        List<CartProductItemEntity> cartProductItems = underTest.findAll();

        assertThat(cartProductItems)
                .hasSize(2)
                .extracting(
                        CartProductItemEntity::getUid,
                        CartProductItemEntity::getCart,
                        CartProductItemEntity::getProduct,
                        CartProductItemEntity::getQuantity,
                        CartProductItemEntity::getCreatedAt
                )
                .containsExactlyInAnyOrder(
                        tuple(uid, cart, product, quantityOfCartProductItem, instant),
                        tuple(secondUid, cart, product, quantityOfCartProductItem, instant)
                );
    }

    @Test
    void whenMultipleCartProductItemsWithDifferingProductsForTheSameCart_thenAllCanBeRetrieved() {
        // First cart product item
        UUID uid = saveCartProductItem(cart, product, quantityOfCartProductItem, instant);

        // Second cart product item for the same cart but different product
        UUID secondUid = saveCartProductItem(cart, anotherProduct, quantityOfAnotherCartProductItem, instant);


        List<CartProductItemEntity> cartProductItems = underTest.findAll();

        assertThat(cartProductItems)
                .hasSize(2)
                .extracting(
                        CartProductItemEntity::getUid,
                        CartProductItemEntity::getCart,
                        CartProductItemEntity::getProduct,
                        CartProductItemEntity::getQuantity,
                        CartProductItemEntity::getCreatedAt
                )
                .containsExactlyInAnyOrder(
                        tuple(uid, cart, product, quantityOfCartProductItem, instant),
                        tuple(secondUid, cart, anotherProduct, quantityOfAnotherCartProductItem, instant)
                );
    }

    @Test
    void whenMultipleCartProductItemsForTheSameProduct_thenAllCanBePersistedAndRetrieved() {
        int secondQuantityOfProduct = Instancio.create(int.class);

        // First cart product item
        UUID uid = saveCartProductItem(cart, product, quantityOfCartProductItem, instant);

        // Second cart product item for the same cart but different product
        UUID secondUid = saveCartProductItem(cart, product, secondQuantityOfProduct, instant);


        List<CartProductItemEntity> cartProductItems = underTest.findAll();

        assertThat(cartProductItems)
                .hasSize(2)
                .extracting(
                        CartProductItemEntity::getUid,
                        CartProductItemEntity::getCart,
                        CartProductItemEntity::getProduct,
                        CartProductItemEntity::getQuantity,
                        CartProductItemEntity::getCreatedAt
                )
                .containsExactlyInAnyOrder(
                        tuple(uid, cart, product, quantityOfCartProductItem, instant),
                        tuple(secondUid, cart, product, secondQuantityOfProduct, instant)
                );
    }

    @Test
    void whenCartProductItemForNonExistingCart_thenPersistenceFails() {
        CartEntity nonExistingCart = Instancio.of(CartEntity.class)
                .set(field("sid"), null) // required for auto generation
                .create();

        assertThatThrownBy(() -> saveCartProductItem(nonExistingCart, product, quantityOfCartProductItem, instant))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void whenCartProductItemForNonExistingProduct_thenPersistenceFails() {
        ProductEntity nonExistingProduct = Instancio.of(ProductEntity.class)
                .set(field("sid"), null) // required for auto generation
                .set(field("price"), BigDecimal.ONE) // required for positive value db constraint
                .create();

        assertThatThrownBy(() -> saveCartProductItem(cart, nonExistingProduct, quantityOfCartProductItem, instant))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void whenCartProductItemQuantityBelowOne_thenPersistenceFails() {
        assertThatThrownBy(() -> saveCartProductItem(cart, product, 0, instant))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private UUID saveCartProductItem(CartEntity cart, ProductEntity product, int quantity, Instant createdAt) {
        UUID uid = UUID.randomUUID();
        underTest.saveCartProduct(
                uid,
                cart.getUserUid(),
                product.getUid(),
                quantity,
                createdAt
        );
        return uid;
    }

    @Test
    void tt() {
        underTest.getCartProductItemEntitiesByCart_UserUid(UUID.randomUUID());
    }
}
