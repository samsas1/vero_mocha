package com.coffee.reporting;

import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReportingRepositoryIntTest {

    @Test
    void whenNoOrders_thenMostUsedToppingPerDrinkIsEmpty() {
        //TODO
    }

    @Test
    void whenNoToppingsForProductOrdered_thenReturnNullWithProductInfoAndQuantityZero() {
        //TODO
    }

    @Test
    void whenToppingOrderedForProduct_thenToppingQuantityIsProductQuantityTimesTopping() {
        //TODO
    }

    @Test
    void whenMultipleToppingsOrderedForMultipleProducts_thenToppingQuantityIsProductQuantityTimesToppingPerProduct() {
        //TODO
    }
}
