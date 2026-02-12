package com.coffee.reporting.custom.query;

import com.coffee.reporting.entity.ProductOrderCount;
import com.coffee.reporting.entity.ToppingOrderCountPerProduct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class ReportingRepository {

    /**
     * Get the total quantity of each product ordered across all customer orders.
     */
    private static final String SELECT_PRODUCT_ORDER_COUNTS = """
                    SELECT
                        p.uid AS product_uid,
                        p.name AS product_name,
                        SUM(copi.quantity) AS total_product_order_quantity
                    FROM product p
                    LEFT JOIN customer_order_product_item copi ON copi.product_sid = p.sid
                    GROUP BY p.uid, p.name
            """;

    /**
     * Get the total quantity of each topping ordered for each product across all customer orders.
     * If no toppings were ordered for a product, the total quantity should be returned as 0 and identifiers as null.
     * Else, it returns n rows per product where n is the number of toppings with orders linked to that product.
     */
    private static final String SELECT_TOPPING_ORDER_COUNTS_PER_PRODUCTS = """
                    WITH topping_order_counts AS (
                        SELECT
                            p.sid AS product_sid,
                            t.sid AS topping_sid,
                            SUM(coti.quantity * copi.quantity) AS total_topping_order_quantity_per_product
                        FROM customer_order_product_item copi
                        JOIN product p ON copi.product_sid = p.sid
                        JOIN customer_order_topping_item coti ON coti.customer_order_product_item_sid = copi.sid
                        JOIN topping t ON coti.topping_sid = t.sid
                        GROUP BY p.sid, t.sid)
                    SELECT
                        p.uid AS product_uid,
                        p.name AS product_name,
                        t.uid AS topping_uid,
                        t.name AS topping_name,
                        COALESCE(toc.total_topping_order_quantity_per_product, 0) AS total_topping_order_quantity_per_product
                    FROM product p
                    LEFT JOIN topping_order_counts toc ON p.sid = toc.product_sid
                    LEFT JOIN topping t ON toc.topping_sid = t.sid
            
            """;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public List<ProductOrderCount> listProductOrderCounts() {
        return jdbcTemplate.query(SELECT_PRODUCT_ORDER_COUNTS, (rs, rowNum) ->
                new ProductOrderCount(
                        rs.getObject("product_uid", UUID.class),
                        rs.getString("product_name"),
                        rs.getInt("total_product_order_quantity")
                )
        );
    }

    @Transactional
    public List<ToppingOrderCountPerProduct> listToppingOrderCountsPerProducts() {
        return jdbcTemplate.query(SELECT_TOPPING_ORDER_COUNTS_PER_PRODUCTS, (rs, rowNum) ->
                new ToppingOrderCountPerProduct(
                        rs.getObject("product_uid", UUID.class),
                        rs.getObject("topping_uid", UUID.class),
                        rs.getString("topping_name"),
                        rs.getInt("total_topping_order_quantity_per_product")
                )
        );
    }

}

