package com.coffee.order.custom.query.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class OrderProductItemBatchRepositoryImpl implements OrderProductItemBatchRepository {

    private static final String BATCH_INSERT = """
                        INSERT INTO customer_order_topping_item (
                                                                 uid, 
                                                                 customer_order_sid,
                                                                 product_sid, 
                                                                 original_price_per_product, 
                                                                 quantity,
                                                                 created_at,
                                                                 updated_at
                                                                 ) 
                        VALUES (?,
                        SELECT sid FROM customer_order WHERE ? = orderUid,
                        SELECT sid FROM product WHERE ? = productUid,
                        SELECT price FROM product WHERE ? = productUid,
                         ?,
                         ?,
                         ?)
            """;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void saveOrderProduct(List<OrderProductItem> cartToppingItems) {
        List<Object[]> batchArgs = cartToppingItems.stream()
                .map(p -> new Object[]{
                        p.uid(),
                        p.orderUid(),
                        p.productUid(),
                        p.productUid(),
                        p.quantity(),
                        p.createdAt(),
                        p.updatedAt()
                })
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(BATCH_INSERT, batchArgs);
    }

}
