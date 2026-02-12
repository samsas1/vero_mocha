package com.coffee.reporting;

import com.coffee.admin.ProductToppingCountResponse;
import com.coffee.admin.ProductToppingCountsResponse;
import com.coffee.admin.ToppingPerProductCountResponse;
import com.coffee.reporting.custom.query.ReportingRepository;
import com.coffee.reporting.entity.ProductOrderCount;
import com.coffee.reporting.entity.ToppingOrderCountPerProduct;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.instancio.Select.field;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReportingServiceTest {

    private final ReportingRepository reportingRepository = mock(ReportingRepository.class);
    private ReportingService underTest;

    @BeforeEach
    void setUp() {
        underTest = new ReportingService(reportingRepository);
    }

    @Test
    void whenProductQuantityIsZero_thenAverageToppingCountShouldBeZero() {
        when(reportingRepository.listProductOrderCounts())
                .thenReturn(List.of());
        when(reportingRepository.listToppingOrderCountsPerProducts())
                .thenReturn(List.of());

        ProductToppingCountsResponse response = underTest.generateMostUsedToppingPerProductReport();
        assertThat(response.productToppingCounts())
                .hasSize(1)
                .extracting(
                        ProductToppingCountResponse::totalOrdered
                )
                .containsExactly(0);
    }

    @Test
    void whenProductItemExistsButNoToppingItemsAreLinked_thenReturnCorrectlyMappedProductResponse() {
        ProductOrderCount productOrderCount = Instancio.create(ProductOrderCount.class);
        when(reportingRepository.listProductOrderCounts())
                .thenReturn(List.of(productOrderCount));
        when(reportingRepository.listToppingOrderCountsPerProducts())
                .thenReturn(List.of());

        ProductToppingCountsResponse response = underTest.generateMostUsedToppingPerProductReport();

        assertThat(response.productToppingCounts())
                .hasSize(1)
                .extracting(
                        ProductToppingCountResponse::productUid,
                        ProductToppingCountResponse::productName,
                        ProductToppingCountResponse::totalOrdered
                )
                .containsExactly(
                        tuple(
                                productOrderCount.productUid(),
                                productOrderCount.productName(),
                                productOrderCount.totalProductOrderQuantity()
                        )
                );
    }

    @Test
    void whenProductItemExistsWithAToppingItems_thenReturnCorrectlyMappedProductResponse() {
        int productQuantity = 5;
        int toppingQuantity = 2;
        // 2 toppings over 5 products = 0.4 toppings on average
        BigDecimal averageToppingCount = BigDecimal.valueOf(0.4).setScale(2);
        ProductOrderCount productOrderCount = Instancio.of(ProductOrderCount.class)
                .set(field("totalProductOrderQuantity"), productQuantity).create();

        ToppingOrderCountPerProduct toppingOrderCountPerProduct = Instancio.of(ToppingOrderCountPerProduct.class)
                .set(field("productUid"), productOrderCount.productUid())
                .set(field("totalToppingOrderPerProductQuantity"), toppingQuantity).create();

        when(reportingRepository.listProductOrderCounts())
                .thenReturn(List.of(productOrderCount));
        when(reportingRepository.listToppingOrderCountsPerProducts())
                .thenReturn(List.of(toppingOrderCountPerProduct));

        ProductToppingCountsResponse response = underTest.generateMostUsedToppingPerProductReport();

        assertThat(response.productToppingCounts())
                .hasSize(1)
                .extracting(
                        ProductToppingCountResponse::productUid,
                        ProductToppingCountResponse::productName,
                        ProductToppingCountResponse::totalOrdered
                )
                .containsExactly(
                        tuple(
                                productOrderCount.productUid(),
                                productOrderCount.productName(),
                                productOrderCount.totalProductOrderQuantity()
                        )
                );
        assertThat(response.productToppingCounts().getFirst().toppingCounts())
                .hasSize(1)
                .extracting(
                        ToppingPerProductCountResponse::toppingUid,
                        ToppingPerProductCountResponse::toppingName,
                        ToppingPerProductCountResponse::totalOrderedForProduct,
                        ToppingPerProductCountResponse::averageOrderedForProduct
                )
                .containsExactly(
                        tuple(
                                toppingOrderCountPerProduct.toppingUid(),
                                toppingOrderCountPerProduct.toppingName(),
                                toppingOrderCountPerProduct.totalToppingOrderPerProductQuantity(),
                                averageToppingCount
                        )
                );
    }
}
