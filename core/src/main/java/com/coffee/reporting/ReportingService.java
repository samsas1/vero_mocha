package com.coffee.reporting;

import com.coffee.admin.ProductToppingCountResponse;
import com.coffee.admin.ProductToppingCountsResponse;
import com.coffee.admin.ToppingPerProductCountResponse;
import com.coffee.reporting.custom.query.ReportingRepository;
import com.coffee.reporting.entity.ProductOrderCount;
import com.coffee.reporting.entity.ToppingOrderCountPerProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportingService {

    private static final Logger log = LoggerFactory.getLogger(ReportingService.class);
    @Autowired
    private final ReportingRepository reportingRepository;

    public ReportingService(ReportingRepository reportingRepository) {
        this.reportingRepository = reportingRepository;
    }

    public ProductToppingCountsResponse generateMostUsedToppingPerProductReport() {
        // Fetch product counts
        List<ProductOrderCount> productCounts = reportingRepository
                .listProductOrderCounts();
        // Fetch toppings counts per product
        List<ToppingOrderCountPerProduct> toppingPerProductCounts = reportingRepository
                .listToppingOrderCountsPerProducts();
        // Generate a mapping between product and its topping counts
        Map<UUID, List<ToppingOrderCountPerProduct>> toppingOrderCountToProduct =
                toppingPerProductCounts.stream()
                        .collect(Collectors.groupingBy(ToppingOrderCountPerProduct::productUid));
        // Collect to external response
        List<ProductToppingCountResponse> externalizedCounts = productCounts.stream()
                .map(product -> map(product, toppingOrderCountToProduct)).toList();

        return new ProductToppingCountsResponse(externalizedCounts);
    }

    private BigDecimal getAverageToppingCountPerProduct(
            UUID productUid,
            UUID toppingUid,
            int productQuantity,
            int toppingQuantity
    ) {
        // Should not happen as no product would be returned with zero quantity
        // But log in case it does and return zero in reportin
        if (productQuantity == 0) {
            log.error("Product quantity found top be zero is zero for product {} and topping {}", productUid, toppingUid);
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(toppingQuantity)
                .divide(BigDecimal.valueOf(productQuantity), 2, RoundingMode.HALF_UP).setScale(2);
    }

    private ProductToppingCountResponse map(ProductOrderCount productOrderCount,
                                            Map<UUID, List<ToppingOrderCountPerProduct>> toppingOrderCountToProduct
    ) {
        return new ProductToppingCountResponse(productOrderCount.productUid(),
                productOrderCount.productName(),
                productOrderCount.totalProductOrderQuantity(),
                toppingOrderCountToProduct
                        .getOrDefault(productOrderCount.productUid(), List.of())
                        .stream()
                        .map(topping -> map(productOrderCount, topping))
                        .toList());
    }

    private ToppingPerProductCountResponse map(ProductOrderCount productOrderCount,
                                               ToppingOrderCountPerProduct toppingOrderCountPerProduct
    ) {
        return new ToppingPerProductCountResponse(
                toppingOrderCountPerProduct.toppingUid(),
                toppingOrderCountPerProduct.toppingName(),
                toppingOrderCountPerProduct.totalToppingOrderPerProductQuantity(),
                getAverageToppingCountPerProduct(
                        toppingOrderCountPerProduct.productUid(),
                        toppingOrderCountPerProduct.toppingUid(),
                        productOrderCount.totalProductOrderQuantity(),
                        toppingOrderCountPerProduct.totalToppingOrderPerProductQuantity())
        );
    }
}
