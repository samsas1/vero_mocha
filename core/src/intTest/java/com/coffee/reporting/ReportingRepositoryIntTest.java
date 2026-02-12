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
import com.coffee.reporting.custom.query.ReportingRepository;
import com.coffee.reporting.entity.ProductOrderCount;
import com.coffee.reporting.entity.ToppingOrderCountPerProduct;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.instancio.Select.field;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class ReportingRepositoryIntTest {

    @Autowired
    private ReportingRepository underTest;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ToppingRepository toppingRepository;

    @Autowired
    private OrderProductItemRepository orderProductItemRepository;

    @Autowired
    private OrderToppingItemRepository orderToppingItemRepository;

    private ProductEntity productA;
    private ProductEntity productB;
    private ToppingEntity toppingY;
    private ToppingEntity toppingZ;
    private CustomerOrderEntity order;

    @BeforeEach
    void setUp() {
        productA = Instancio.of(ProductEntity.class)
                .set(field("sid"), null)
                .set(field("price"), BigDecimal.ZERO) // Price set required due to price >= 0 constraint
                .create();
        productRepository.save(productA);

        productB = Instancio.of(ProductEntity.class)
                .set(field("sid"), null)
                .set(field("price"), BigDecimal.ZERO) // Price set required due to price >= 0 constraint
                .create();
        productRepository.save(productB);

        toppingY = Instancio.of(ToppingEntity.class)
                .set(field("sid"), null)
                .set(field("price"), BigDecimal.ZERO) // Price set required due to price >= 0 constraint
                .create();
        toppingRepository.save(toppingY);

        toppingZ = Instancio.of(ToppingEntity.class)
                .set(field("sid"), null)
                .set(field("price"), BigDecimal.ZERO) // Price set required due to price >= 0 constraint
                .create();

        toppingRepository.save(toppingZ);

        // Saving an order does not interfere with no product orders existing
        // This is because the order is not yet linked to any product orders
        order = Instancio.of(CustomerOrderEntity.class)
                .set(field("sid"), null)
                .set(field("originalPrice"), BigDecimal.ZERO) // Price set required due to price >= 0 constraint
                .set(field("finalPrice"), BigDecimal.ZERO) // Price set required due to price >= 0 constraint
                .create();

        orderRepository.save(order);
    }

    @Test
    void whenNoProductOrders_thenListProductOrderCountsReturnsProductsWithZeroCounts() {
        List<ProductOrderCount> result = underTest.listProductOrderCounts();

        assertThat(result)
                .hasSize(2)
                .extracting(
                        ProductOrderCount::productUid,
                        ProductOrderCount::productName,
                        ProductOrderCount::totalProductOrderQuantity
                )
                .containsExactlyInAnyOrder(
                        tuple(
                                productA.getUid(),
                                productA.getName(),
                                0
                        ),
                        tuple(
                                productB.getUid(),
                                productB.getName(),
                                0
                        )
                );
    }

    @Test
    void whenProductOrdered_thenListProductOrderCountsReturnsTheProductWithCorrectCountAndZeroInOtherProduct() {
        int productItemQty = Instancio.create(int.class);
        saveOrderProductItem(productItemQty, order, productA);

        List<ProductOrderCount> result = underTest.listProductOrderCounts();

        assertThat(result)
                .hasSize(2)
                .extracting(
                        ProductOrderCount::productUid,
                        ProductOrderCount::productName,
                        ProductOrderCount::totalProductOrderQuantity
                )
                .containsExactlyInAnyOrder(
                        tuple(
                                productA.getUid(),
                                productA.getName(),
                                productItemQty
                        ),
                        tuple(
                                productB.getUid(),
                                productB.getName(),
                                0
                        )
                );
    }

    @Test
    void whenProductOrderedMultipleTimes_thenListProductOrderCountsReturnsTheProductWithSummedCountAndZeroInOtherProduct() {
        int productItemQty1 = 3;
        saveOrderProductItem(productItemQty1, order, productA);

        int productItemQty2 = 5;
        saveOrderProductItem(productItemQty2, order, productA);

        // Total is 3 + 5 = 8
        int totalProductItemQty = 8;

        List<ProductOrderCount> result = underTest.listProductOrderCounts();

        assertThat(result)
                .hasSize(2)
                .extracting(
                        ProductOrderCount::productUid,
                        ProductOrderCount::productName,
                        ProductOrderCount::totalProductOrderQuantity
                )
                .containsExactlyInAnyOrder(
                        tuple(
                                productA.getUid(),
                                productA.getName(),
                                totalProductItemQty
                        ),
                        tuple(
                                productB.getUid(),
                                productB.getName(),
                                0
                        )
                );
    }

    @Test
    void whenMultipleProductsOrdered_thenListProductOrderCountsReturnsAllProductsWithCorrectCounts() {
        int productAItemQty = Instancio.create(int.class);
        saveOrderProductItem(productAItemQty, order, productA);

        int productBItemQty = Instancio.create(int.class);
        saveOrderProductItem(productBItemQty, order, productB);

        List<ProductOrderCount> result = underTest.listProductOrderCounts();
        assertThat(result).hasSize(2).extracting(
                        ProductOrderCount::productUid,
                        ProductOrderCount::productName,
                        ProductOrderCount::totalProductOrderQuantity)
                .containsExactlyInAnyOrder(
                        tuple(
                                productA.getUid(),
                                productA.getName(),
                                productAItemQty),
                        tuple(
                                productB.getUid(),
                                productB.getName(),
                                productBItemQty)
                );
    }


    @Test
    void whenProductOrderedWithTopping_thenListToppingOrderCountsPerProductsReturnsProductsWithToppingCountMultipliedByProductCount() {
        int productItemQty = 2;
        CustomerOrderProductItemEntity orderProductItem = saveOrderProductItem(productItemQty, order, productA);

        int toppingItemQty = 6;
        saveOrderToppingItem(toppingItemQty, orderProductItem, toppingY);

        // Total is 2 * 6 = 12 as the order contains 2 counts of a product with 6 toppings
        int totalToppingOrderQtyPerProduct = 12;


        List<ToppingOrderCountPerProduct> result = underTest.listToppingOrderCountsPerProducts();

        assertThat(result)
                .hasSize(2)
                .extracting(
                        ToppingOrderCountPerProduct::productUid,
                        ToppingOrderCountPerProduct::toppingUid,
                        ToppingOrderCountPerProduct::toppingName,
                        ToppingOrderCountPerProduct::totalToppingOrderPerProductQuantity
                )
                .containsExactlyInAnyOrder(
                        tuple(
                                productA.getUid(),
                                toppingY.getUid(),
                                toppingY.getName(),
                                totalToppingOrderQtyPerProduct
                        ),
                        tuple(
                                productB.getUid(),
                                null,
                                null,
                                0
                        )
                );
    }


    @Test
    void whenMultipleProductsOrderedWithMultipleToppings_thenListToppingOrderCountsPerProductsReturnsProductsWithToppingCountMultipliedByProductCount() {
        // Product A
        // Item 1
        int productAItemQty = 2;
        CustomerOrderProductItemEntity productAItem = saveOrderProductItem(productAItemQty, order, productA);

        int toppingYProductAQty1 = 2;
        saveOrderToppingItem(toppingYProductAQty1, productAItem, toppingY);
        int toppingZProductAQty1 = 3;
        saveOrderToppingItem(toppingZProductAQty1, productAItem, toppingZ);

        // Item 2
        int productAItem2Qty = 1;
        CustomerOrderProductItemEntity productAItem2 = saveOrderProductItem(productAItem2Qty, order, productA);

        int toppingYProductAQty2 = 4;
        saveOrderToppingItem(toppingYProductAQty2, productAItem2, toppingY);

        // 2 productA with 2 toppingY +  1 productA with 4 toppingZ = 4 + 4 = 8
        int expectedProductAToppingYCount = 8;
        // 2 productA with 3 toppingZ = 6
        int expectedProductAToppingZCount = 6;

        // ProductB
        // Item 1
        int productBItemQty = 5;
        CustomerOrderProductItemEntity productBItem = saveOrderProductItem(productBItemQty, order, productB);

        int toppingYQty2 = 2;
        saveOrderToppingItem(toppingYQty2, productBItem, toppingY);

        // 5 productB with 2 toppingY = 10
        int expectedProductBToppingYCount = 10;

        List<ToppingOrderCountPerProduct> result = underTest.listToppingOrderCountsPerProducts();

        assertThat(result)
                .hasSize(3)
                .extracting(
                        ToppingOrderCountPerProduct::productUid,
                        ToppingOrderCountPerProduct::toppingUid,
                        ToppingOrderCountPerProduct::toppingName,
                        ToppingOrderCountPerProduct::totalToppingOrderPerProductQuantity
                )
                .containsExactlyInAnyOrder(
                        tuple(
                                productA.getUid(),
                                toppingY.getUid(),
                                toppingY.getName(),
                                expectedProductAToppingYCount
                        ),
                        tuple(
                                productA.getUid(),
                                toppingZ.getUid(),
                                toppingZ.getName(),
                                expectedProductAToppingZCount
                        ),
                        tuple(
                                productB.getUid(),
                                toppingY.getUid(),
                                toppingY.getName(),
                                expectedProductBToppingYCount
                        )
                );
    }

    private CustomerOrderProductItemEntity saveOrderProductItem(int productItemQuantity, CustomerOrderEntity order, ProductEntity product) {
        CustomerOrderProductItemEntity productItem = Instancio.of(CustomerOrderProductItemEntity.class)
                .set(field("sid"), null)
                .set(field("quantity"), productItemQuantity)
                .set(field("originalPricePerProduct"), BigDecimal.ZERO) // Price set required due to price >= 0 constraint
                .set(field("customerOrder"), order)
                .set(field("product"), product)
                .create();
        return orderProductItemRepository.save(productItem);
    }

    private CustomerOrderToppingItemEntity saveOrderToppingItem(int toppingItemQuantity,
                                                                CustomerOrderProductItemEntity productItem,
                                                                ToppingEntity topping) {
        CustomerOrderToppingItemEntity toppingItem1 = Instancio.of(CustomerOrderToppingItemEntity.class)
                .set(field("sid"), null)
                .set(field("customerOrderProductItem"), productItem)
                .set(field("originalPricePerTopping"), BigDecimal.ZERO) // Price set required due to price >= 0 constraint
                .set(field("quantity"), toppingItemQuantity)
                .set(field("topping"), topping)
                .create();
        return orderToppingItemRepository.save(toppingItem1);
    }
}
