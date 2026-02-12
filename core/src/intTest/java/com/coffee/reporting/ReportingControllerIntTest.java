package com.coffee.reporting;

import com.coffee.item.ProductRepository;
import com.coffee.item.ToppingRepository;
import com.coffee.item.entity.database.ProductEntity;
import com.coffee.item.entity.database.ToppingEntity;
import com.coffee.order.OrderProductItemRepository;
import com.coffee.order.OrderRepository;
import com.coffee.order.OrderToppingItemRepository;
import com.coffee.order.entity.database.CustomerOrderEntity;
import com.coffee.order.entity.database.CustomerOrderProductItemEntity;
import com.coffee.order.entity.database.CustomerOrderToppingItemEntity;
import jakarta.transaction.Transactional;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.instancio.Select.field;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ReportingControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ToppingRepository toppingRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductItemRepository orderProductItemRepository;

    @Autowired
    private OrderToppingItemRepository orderToppingItemRepository;

    private ProductEntity cappuccino;
    private ToppingEntity vanilla;
    private ToppingEntity caramel;

    private int cappuccinoOrder1Qty;
    private int cappuccinoOrder1VanillaQty;
    private int cappuccinoOrder1CaramelQty;


    @BeforeEach
    public void setUp() {
        cappuccino = createProduct("Cappuccino");
        vanilla = createTopping("Vanilla");
        caramel = createTopping("Caramel");

    }

    @Test
    public void testReportingControllerWithMultipleProductsAndToppings() throws Exception {
        cappuccinoOrder1Qty = 2;
        cappuccinoOrder1VanillaQty = 1;
        cappuccinoOrder1CaramelQty = 2;

        // 2 cappuccino with 1 vanilla each
        int cappuccinoVanillaCount = 2;
        // Average vanilla per cappuccino = 1 since product toppings are the same
        double cappuccinoVanillaAverage = 1.0;

        // 2 cappuccino with 2 caramel each
        int cappuccinoCaramelCount = 4;
        // Average caramel per cappuccino = 2 since product toppings are the same
        double cappuccinoCaramelAverage = 2.0;

        // Create orders with various products and toppings
        createOrderWithProductsAndToppings();

        // Call the reporting endpoint
        mockMvc.perform(get("/reports/toppings/most-used"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.productToppingCounts", hasSize(equalTo(1))))
                .andExpect(jsonPath("$.productToppingCounts[0].productName", equalTo("Cappuccino")))
                .andExpect(jsonPath("$.productToppingCounts[0].totalOrdered", equalTo(cappuccinoOrder1Qty)))
                .andExpect(jsonPath("$.productToppingCounts[0].toppingCounts[*].toppingName",
                        allOf(hasItem("Vanilla"), hasItem("Caramel"))))
                // Verify vanilla
                .andExpect(jsonPath("$.productToppingCounts[0].toppingCounts[?(@.toppingName == 'Vanilla')].totalOrderedForProduct",
                        hasItem(cappuccinoVanillaCount)))
                // Verify average vanilla
                .andExpect(jsonPath("$.productToppingCounts[0].toppingCounts[?(@.toppingName == 'Vanilla')].averageOrderedForProduct",
                        hasItem(cappuccinoVanillaAverage)))
                // Verify caramel count
                .andExpect(jsonPath("$.productToppingCounts[0].toppingCounts[?(@.toppingName == 'Caramel')].totalOrderedForProduct",
                        hasItem(cappuccinoCaramelCount)))
                // Verify average caramel
                .andExpect(jsonPath("$.productToppingCounts[0].toppingCounts[?(@.toppingName == 'Caramel')].averageOrderedForProduct",
                        hasItem(cappuccinoCaramelAverage)));
    }

    private void createOrderWithProductsAndToppings() {
        CustomerOrderEntity order1 = createOrder();
        CustomerOrderProductItemEntity cappuccinoItem1 = createOrderProductItem(
                order1, cappuccino, cappuccinoOrder1Qty);
        createOrderToppingItem(cappuccinoItem1, caramel, cappuccinoOrder1CaramelQty);
        createOrderToppingItem(cappuccinoItem1, vanilla, cappuccinoOrder1VanillaQty);
    }

    private ProductEntity createProduct(String name) {
        ProductEntity product = Instancio.of(ProductEntity.class)
                .set(field("sid"), null) // required for auto generation
                .set(field("name"), name)
                .set(field("price"), BigDecimal.ZERO) // required for positive value db constraint, but not relevant for this test
                .create();
        return productRepository.save(product);
    }

    private ToppingEntity createTopping(String name) {
        ToppingEntity topping = Instancio.of(ToppingEntity.class)
                .set(field("sid"), null) // required for auto generation
                .set(field("name"), name)
                .set(field("price"), BigDecimal.ZERO) // required for positive value db constraint, but not relevant for this test
                .create();
        return toppingRepository.save(topping);
    }

    private CustomerOrderEntity createOrder() {
        CustomerOrderEntity order = Instancio.of(CustomerOrderEntity.class)
                .set(field("sid"), null) // required for auto generation
                .set(field("originalPrice"), BigDecimal.ZERO) // required for positive value db constraint
                .set(field("finalPrice"), BigDecimal.ZERO) // required for positive value db constraint
                .create();
        return orderRepository.save(order);
    }

    private CustomerOrderProductItemEntity createOrderProductItem(
            CustomerOrderEntity order,
            ProductEntity product,
            int quantity
    ) {
        CustomerOrderProductItemEntity item = Instancio.of(CustomerOrderProductItemEntity.class)
                .set(field("sid"), null) // required for auto generation
                .set(field("customerOrder"), order)
                .set(field("product"), product)
                .set(field("quantity"), quantity)
                .set(field("originalPricePerProduct"), product.getPrice()) // required for positive value db constraint
                .create();
        return orderProductItemRepository.save(item);
    }

    private CustomerOrderToppingItemEntity createOrderToppingItem(
            CustomerOrderProductItemEntity productItem,
            ToppingEntity topping,
            int quantity
    ) {
        CustomerOrderToppingItemEntity item = Instancio.of(CustomerOrderToppingItemEntity.class)
                .set(field("sid"), null) // required for auto generation
                .set(field("customerOrderProductItem"), productItem)
                .set(field("topping"), topping)
                .set(field("quantity"), quantity)
                .set(field("originalPricePerTopping"), topping.getPrice()) // required for positive value db constraint
                .create();
        return orderToppingItemRepository.save(item);
    }
}
