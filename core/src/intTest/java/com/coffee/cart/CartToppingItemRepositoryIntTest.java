package com.coffee.cart;

import com.coffee.item.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class CartToppingItemRepositoryIntTest {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartProductItemRepository cartProductItemRepository;


    @Autowired
    CartToppingItemRepository cartToppingItemRepository;

    @Test
    void whenCartToppingItemIsPersisted_thenItCanBeRetrieved() {
        //TODO
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

