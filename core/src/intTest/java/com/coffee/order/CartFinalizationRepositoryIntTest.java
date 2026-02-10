package com.coffee.order;

import com.coffee.cart.CartProductItemRepository;
import com.coffee.cart.CartRepository;
import com.coffee.cart.CartToppingItemRepository;
import com.coffee.cart.custom.query.batch.CartToppingItemBatchRepository.CartToppingItem;
import com.coffee.cart.entity.CartEntity;
import com.coffee.item.ProductRepository;
import com.coffee.item.ToppingRepository;
import com.coffee.item.entity.ProductEntity;
import com.coffee.item.entity.ToppingEntity;
import com.coffee.order.custom.query.CartFinalizationRepository;
import com.coffee.order.entity.CartTotalsEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.instancio.Select.field;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class CartFinalizationRepositoryIntTest {

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

    @Autowired
    CartFinalizationRepository underTest;


    private UUID userUid;
    private UUID otherUserUid;
    private CartEntity cart;
    private CartEntity otherCart;
    private ProductEntity product;
    private ToppingEntity topping;
    private ToppingEntity anotherTopping;
    private int quantityOfCartProductItem;
    private Instant instant;
    private int quantityOfCartToppingItem;
    private int quantityOfAnotherCartToppingItem;


    @BeforeEach
    void setUp() {
        // ------------------------ Set up preemptive database entities ------------------------
        // Initialize UUIDs for users to ensure non-null userUid on CartEntity
        userUid = UUID.randomUUID();
        otherUserUid = UUID.randomUUID();

        // Set up carts
        cart = Instancio.of(CartEntity.class)
                .set(field("userUid"), userUid)
                .set(field("sid"), null) // required for auto generation
                .create();
        cartRepository.save(cart);
        otherCart = Instancio.of(CartEntity.class)
                .set(field("userUid"), otherUserUid)
                .set(field("sid"), null) // required for auto generation
                .create();
        cartRepository.save(otherCart);

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

        // ------------------------ End set up preemptive database entities ------------------------


        // Prepare other entity values without writing anything to db
        // At the start of the test we only have products and toppings in the database
        quantityOfCartProductItem = Instancio.create(int.class);
        quantityOfCartToppingItem = Instancio.create(int.class);
        quantityOfAnotherCartToppingItem = Instancio.create(int.class);
        // Truncated to millis to avoid precision loss when comparing with db value
        instant = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    }

    @Test
    void whenCartFinalizationFetchedForEmptyCart_thenResultIsEmpty() {
        assertThat(underTest.listCartTotals(userUid)).isEmpty();
    }


    @Test
    void whenCartFinalizationFetchedForOneProductAndNoToppings_thenResultOnlyContainsProduct() {
        UUID cartProductItemUid = setUpProductItem(cart, quantityOfCartProductItem, instant, product);

        assertThat(underTest.listCartTotals(userUid))
                .extracting(
                        CartTotalsEntity::cartUid,
                        CartTotalsEntity::productItemUid,
                        CartTotalsEntity::toppingItemUid,
                        CartTotalsEntity::productUid,
                        CartTotalsEntity::toppingUid,
                        CartTotalsEntity::productItemQuantity,
                        CartTotalsEntity::toppingItemPerProductItemQuantity,
                        CartTotalsEntity::productPrice,
                        CartTotalsEntity::toppingPrice
                )
                .containsExactly(
                        tuple(
                                cart.getUid(),
                                cartProductItemUid,
                                Optional.empty(),
                                product.getUid(),
                                Optional.empty(),
                                quantityOfCartProductItem,
                                Optional.empty(),
                                product.getPrice(),
                                Optional.empty()
                        )
                );


    }

    @Test
    void whenCartFinalizationFetchedAndTwoCartsHaveProducts_thenOnlyQueriedCartReturned() {

    }


    @Test
    void whenCartFinalizationFetchedForOneProductAndTwoToppingsBelongingToCart_thenResultsAreListedForCart() {
        UUID cartProductItemUid = setUpProductItem(
                cart,
                quantityOfCartProductItem,
                instant,
                product);

        UUID cartToppingItemUid = setUpCartToppingItem(
                quantityOfCartToppingItem,
                cartProductItemUid,
                topping,
                instant
        );

        UUID anotherCartToppingItemUid = setUpCartToppingItem(
                quantityOfAnotherCartToppingItem,
                cartProductItemUid,
                anotherTopping,
                instant
        );

        assertThat(underTest.listCartTotals(userUid))
                .extracting(
                        CartTotalsEntity::cartUid,
                        CartTotalsEntity::productItemUid,
                        CartTotalsEntity::toppingItemUid,
                        CartTotalsEntity::productUid,
                        CartTotalsEntity::toppingUid,
                        CartTotalsEntity::productItemQuantity,
                        CartTotalsEntity::toppingItemPerProductItemQuantity,
                        CartTotalsEntity::productPrice,
                        CartTotalsEntity::toppingPrice
                )
                .containsExactly(
                        tuple(cart.getUid(),
                                cartProductItemUid,
                                Optional.of(cartToppingItemUid),
                                product.getUid(),
                                Optional.of(topping.getUid()),
                                quantityOfCartProductItem,
                                Optional.of(quantityOfCartToppingItem),
                                product.getPrice(),
                                Optional.of(topping.getPrice())
                        ),
                        tuple(
                                cart.getUid(),
                                cartProductItemUid,
                                Optional.of(anotherCartToppingItemUid),
                                product.getUid(),
                                Optional.of(anotherTopping.getUid()),
                                quantityOfCartProductItem,
                                Optional.of(quantityOfAnotherCartToppingItem),
                                product.getPrice(),
                                Optional.of(anotherTopping.getPrice())

                        )
                );
    }

    @Test
    void whenCartFinalizationFetchedForTwoProductsAndTwoToppingsEach_thenResultsAreListedForCart() {

    }

    private UUID setUpProductItem(CartEntity cart,
                                  int quantityOfCartProductItem,
                                  Instant instant,
                                  ProductEntity product) {
        UUID cartProductItemUid = UUID.randomUUID();
        cartProductItemRepository.saveCartProduct(
                cartProductItemUid,
                cart.getUserUid(),
                product.getUid(),
                quantityOfCartProductItem,
                instant
        );
        return cartProductItemUid;
    }

    private UUID setUpCartToppingItem(int quantityOfCartToppingItem,
                                      UUID cartProductItemUid,
                                      ToppingEntity topping,
                                      Instant instant) {
        CartToppingItem cartToppingItem = new CartToppingItem(
                UUID.randomUUID(),
                cartProductItemUid,
                topping.getUid(),
                quantityOfCartToppingItem,
                instant
        );
        cartToppingItemRepository.saveCartProduct(List.of(cartToppingItem));
        return cartToppingItem.uid();
    }


}
