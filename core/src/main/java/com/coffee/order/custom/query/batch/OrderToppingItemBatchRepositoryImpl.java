package com.coffee.order.custom.query.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class OrderToppingItemBatchRepositoryImpl implements OrderToppingItemBatchRepository {


    private static final String BATCH_INSERT = """
                        INSERT INTO customer_order_topping_item (
                                                                 uid, 
                                                                 customer_order_product_item_sid,
                                                                 topping_sid, 
                                                                 original_price_per_topping, 
                                                                 quantity,
                                                                 created_at,
                                                                 updated_at
                                                                 ) 
                        VALUES (?,
                        SELECT sid FROM customer_order_product_item WHERE ? = orderUid,
                        SELECT sid FROM topping WHERE ? = toppingUid,
                        SELECT price FROM topping WHERE ? = topppingUid,
                         ?,
                         ?,
                         ?)
            """;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void saveOrderTopping(List<OrderToppingItem> cartToppingItems) {
        List<Object[]> batchArgs = cartToppingItems.stream()
                .map(p -> new Object[]{
                        p.uid(),
                        p.orderProductItemUid(),
                        p.toppingUid(),
                        p.quantity(),
                        p.createdAt(),
                        p.updatedAt()
                })
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(BATCH_INSERT, batchArgs);
    }
}
