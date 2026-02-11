package com.coffee.reporting.custom.query;

import com.coffee.reporting.entity.MostUsedToppingPerDrink;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class ReportingRepository {

    private static final String SELECT_MOST_USED_TOPPINGS_PER_DRINK =
            """
                            WITH topping_item_breakdown AS (
                                SELECT
                                    p.sid AS product_sid,
                                    p.uid AS product_uid,
                                    p.name AS product_name,
                                    t.uid AS topping_uid,
                                    t.name AS topping_name,
                                    coti.quantity * copi.quantity AS total_topping_quantity
                                FROM customer_order_topping_item coti
                                JOIN customer_order_product_item copi ON coti.customer_order_product_item_sid = copi.sid
                                JOIN topping t ON coti.topping_sid = t.sid
                                JOIN product p ON copi.product_sid = p.sid)
                    
                            SELECT
                                product_uid,
                                product_name,
                                topping_uid,
                                topping_name,
                                COALSECE(SUM(total_topping_quantity), 0) AS total_topping_quantity
                            FROM product p
                            LEFT JOIN topping_item_breakdown tib ON p.sid = tib.product_sid
                            GROUP BY product_uid, product_name, topping_uid, topping_name;
                    """;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Transactional
    public List<MostUsedToppingPerDrink> listMostUsedToppingsForDrinks() {
        return jdbcTemplate.query(SELECT_MOST_USED_TOPPINGS_PER_DRINK, (rs, rowNum) ->
                new MostUsedToppingPerDrink(
                        rs.getObject("product_uid", UUID.class),
                        rs.getString("product_name"),
                        rs.getObject("topping_uid", UUID.class),
                        rs.getString("topping_name"),
                        rs.getInt("total_topping_quantity")
                )
        );
    }
}

