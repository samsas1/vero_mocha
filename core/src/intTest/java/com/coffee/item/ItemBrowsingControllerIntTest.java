package com.coffee.item;

import com.coffee.item.entity.database.ProductEntity;
import com.coffee.item.entity.database.ToppingEntity;
import jakarta.transaction.Transactional;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static com.coffee.item.entity.InternalItemStatus.ACTIVE;
import static com.coffee.item.entity.InternalItemStatus.INACTIVE;
import static org.instancio.Select.field;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ItemBrowsingControllerIntTest {

    private static final String TOPPING_ENDPOINT = "/menu/topping";
    private static final String PRODUCT_ENDPOINT = "/menu/product";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ToppingRepository toppingRepository;

    @Autowired
    private ProductRepository productRepository;

    private ToppingEntity activeTopping;
    private ToppingEntity inactiveTopping;
    private ProductEntity activeProduct;
    private ProductEntity inactiveProduct;

    @Test
    public void whenNoProducts_thenEmptyListReturned() throws Exception {
        mockMvc.perform(get(PRODUCT_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products.length()").value(0));
    }

    @Test
    public void whenNoToppings_thenEmptyListReturned() throws Exception {
        mockMvc.perform(get(TOPPING_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toppings.length()").value(0));
    }

    @Test
    public void whenActiveProductsFetched_thenOnlyActiveProductsReturned() throws Exception {
        setUpProducts();
        mockMvc.perform(get(PRODUCT_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products.length()").value(1))
                .andExpect(jsonPath("$.products[0].price").value(activeProduct.getPrice()))
                .andExpect(jsonPath("$.products[0].name").value(activeProduct.getName()))
                .andExpect(jsonPath("$.products[0].uid").value(activeProduct.getUid().toString()))
                .andExpect(jsonPath("$.products[0].createdAt").exists())
                .andExpect(jsonPath("$.products[0].updatedAt").exists());
    }

    @Test
    public void whenActiveToppingsFetched_thenOnlyActiveProductsReturned() throws Exception {
        setUpToppings();
        mockMvc.perform(get(TOPPING_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toppings.length()").value(1))
                .andExpect(jsonPath("$.toppings[0].price").value(activeTopping.getPrice()))
                .andExpect(jsonPath("$.toppings[0].name").value(activeTopping.getName()))
                .andExpect(jsonPath("$.toppings[0].uid").value(activeTopping.getUid().toString()))
                .andExpect(jsonPath("$.toppings[0].createdAt").exists())
                .andExpect(jsonPath("$.toppings[0].updatedAt").exists());

    }

    private void setUpProducts() {
        activeProduct = Instancio.of(ProductEntity.class)
                .set(field("sid"), null) // required for auto generation
                .set(field("status"), ACTIVE)
                .set(field("price"), BigDecimal.valueOf(3.45)) // required for positive value db constraint
                .create();
        inactiveProduct = Instancio.of(ProductEntity.class)
                .set(field("sid"), null) // required for auto generation
                .set(field("status"), INACTIVE)
                .set(field("price"), BigDecimal.valueOf(4.56)) // required for positive value db constraint
                .create();
        productRepository.save(activeProduct);
        productRepository.save(inactiveProduct);
    }

    private void setUpToppings() {
        activeTopping = Instancio.of(ToppingEntity.class)
                .set(field("sid"), null) // required for auto generation
                .set(field("status"), ACTIVE)
                .set(field("price"), BigDecimal.valueOf(1.23)) // required for positive value db constraint
                .create();
        inactiveTopping = Instancio.of(ToppingEntity.class)
                .set(field("sid"), null) // required for auto generation
                .set(field("status"), INACTIVE)
                .set(field("price"), BigDecimal.valueOf(2.24)) // required for positive value db constraint
                .create();
        toppingRepository.save(activeTopping);
        toppingRepository.save(inactiveTopping);
    }
}
