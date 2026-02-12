package com.coffee.item;

import com.coffee.item.entity.database.ToppingEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ToppingRepositoryIntTest {

    @Autowired
    ToppingRepository underTest;

    @Test
    void whenToppingPersisted_thenItCanBeRetrieved() {
        ToppingEntity topping = Instancio.of(ToppingEntity.class)
                .set(field("sid"), null) // required for auto generation
                .set(field("price"), BigDecimal.ONE) // required for positive value db constraint
                .create();
        underTest.save(topping);

        ToppingEntity fetchedTopping = underTest.findByUid(topping.getUid()).get();

        assertThat(fetchedTopping)
                .isNotNull()
                .extracting(
                        ToppingEntity::getUid,
                        ToppingEntity::getName,
                        ToppingEntity::getPrice,
                        ToppingEntity::getStatus,
                        i -> i.getCreatedAt().truncatedTo(ChronoUnit.MILLIS),
                        i -> i.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS)
                )
                .containsExactly(
                        topping.getUid(),
                        topping.getName(),
                        topping.getPrice(),
                        topping.getStatus(),
                        topping.getCreatedAt().truncatedTo(ChronoUnit.MILLIS),
                        topping.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS)
                );
    }

    @Test
    void whenAttemptingToPersistToppingsWithSameName_thenFails() {
        //TODO
    }

    @Test
    void whenAttemptingToPersistFreeTopping_thenItIsPersisted() {
        //TODO
    }

    @Test
    void whenAttemptingToPersistToppingWithNegativePrice_thenFails() {
        //TODO
    }

    @Test
    void whenAttemptingToPersistProductWithZeroQuantity_thenFails() {
        //TODO
    }

    @Test
    void whenToppingIsUpdated_thenUpdateIsPersisted() {
        //TODO
    }

    @Test
    void whenToppingIsUpdated_thenOtherToppingIsUnaffected() {
        //TODO
    }

    @Test
    void whenToppingIsDeleted_thenItIsRemoved() {
        //TODO
    }

    @Test
    void whenToppingIsDeleted_thenOtherToppingIsStillPresent() {
        //TODO
    }

    // existsLinkedToCartOrOrder tests
    @Test
    void whenCartToppingItemLinkedToToppingAndLinkChecked_thenReturnsTrue() {
        //TODO
    }

    @Test
    void whenCustomerOrderToppingItemLinkedToToppingAndLinkChecked_thenReturnsTrue() {
        //TODO
    }

    @Test
    void whenNoCartOrOrderToppingItemLinkedToToppingAndLinkChecked_thenReturnsFalse() {
        //TODO
    }

    @Test
    void whenCartAndOrderProductItemLinkedToDifferentProductAndLinkChecked_thenReturnsFalse() {
        //TODO
    }


}
