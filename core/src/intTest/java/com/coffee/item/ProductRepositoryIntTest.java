package com.coffee.item;

import com.coffee.item.entity.ProductEntity;
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
public class ProductRepositoryIntTest {

    @Autowired
    ProductRepository productRepository;

    @Test
    void whenProductPersisted_thenItCanBeRetrieved() {
        ProductEntity product = Instancio.of(ProductEntity.class)
                .set(field("sid"), null) // required for auto generation
                .set(field("price"), BigDecimal.ONE) // required for positive value db constraint
                .create();
        productRepository.save(product);

        ProductEntity fetchedProduct = productRepository.findByUid(product.getUid()).get();

        assertThat(fetchedProduct)
                .isNotNull()
                .extracting(
                        ProductEntity::getUid,
                        ProductEntity::getName,
                        ProductEntity::getPrice,
                        ProductEntity::getStatus,
                        i -> i.getCreatedAt().truncatedTo(ChronoUnit.MILLIS),
                        i -> i.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS)
                )
                .containsExactly(
                        product.getUid(),
                        product.getName(),
                        product.getPrice(),
                        product.getStatus(),
                        product.getCreatedAt().truncatedTo(ChronoUnit.MILLIS),
                        product.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS)
                );
    }

    @Test
    void whenAttemptingToPersistProductsWithSameName_thenFails() {
        //TODO
    }


    @Test
    void whenAttemptingToPersistProductWithNegativePrice_thenFails() {
        //TODO
    }

}
