package com.coffee.cart.custom.query.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CartToppingItemBatchRepositoryImpl implements CartToppingItemBatchRepository {

    private static final String BATCH_INSERT = """
                        INSERT INTO cart_topping_item (uid, cart_product_item_sid, topping_sid, quantity, created_at) 
                        VALUES (?,
                        SELECT sid FROM cart_product_item WHERE ? = uid,
                        SELECT sid FROM topping WHERE ? = uid,
                         ?,
                         ?)
            """;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void saveCartProduct(List<CartTopping> cartToppings) {
        List<Object[]> batchArgs = cartToppings.stream()
                .map(p -> new Object[]{
                        p.uid(),
                        p.cartProductItemUid(),
                        p.toppingUid(),
                        p.quantity(),
                        p.createdAt(),
                })
                .collect(Collectors.toList());
        jdbcTemplate.batchUpdate(BATCH_INSERT, batchArgs);
    }
}
