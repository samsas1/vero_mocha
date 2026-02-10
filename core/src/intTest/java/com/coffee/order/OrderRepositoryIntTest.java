package com.coffee.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryIntTest {

    @Test
    void whenOrderInserted_thenItCanBePersisted() {

    }

    @Test
    void whenOrderInsertedWithFreePrices_thenItCanBePersisted() {

    }

    @Test
    void whenOrderInsertedWithOriginalPriceLowerThanFinalPrice_thenPersistenceFails() {

    }

    @Test
    void whenMultipleOrdersInsertedForTheSameUser_thenTheyCanBePersisted() {

    }

    @Test
    void whenMultipleOrdersInsertedForDifferentUsers_thenTheyCanBePersisted() {

    }
}
