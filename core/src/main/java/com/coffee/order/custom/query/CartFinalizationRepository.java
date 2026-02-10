package com.coffee.order.custom.query;

import com.coffee.order.entity.database.CartItemTableEntryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CartFinalizationRepository {

    private static final String FETCH_CART_TOTALS = """
                SELECT
                    c.uid AS cart_uid,
                    cpi.uid AS product_item_uid,
                    cti.uid AS topping_item_uid,
                    p.uid AS product_uid,
                    t.uid AS topping_uid,
                    cpi.quantity AS product_item_quantity,
                    COALESCE(cti.quantity, 0) AS topping_item_per_product_item_quantity,
                    p.price   AS product_price,
                    COALESCE(t.price, 0)   AS topping_price
                FROM cart c
                    JOIN public.cart_product_item cpi on c.sid = cpi.cart_sid
                    JOIN product p ON cpi.product_sid = p.sid
                    LEFT JOIN public.cart_topping_item cti on cpi.sid = cti.cart_product_item_sid
                    LEFT JOIN public.topping t on cti.topping_sid = t.sid
                WHERE c.user_uid = ?
            """;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Transactional
    public List<CartItemTableEntryEntity> listCartItemTable(UUID userUid) {
        return jdbcTemplate.query(
                FETCH_CART_TOTALS,
                new Object[]{userUid},
                (rs, rowNum) -> new CartItemTableEntryEntity(
                        UUID.fromString(rs.getString("cart_uid")),
                        UUID.fromString(rs.getString("product_item_uid")),
                        Optional.ofNullable(rs.getString("topping_item_uid")).map(UUID::fromString),
                        UUID.fromString(rs.getString("product_uid")),
                        Optional.ofNullable(rs.getString("topping_uid")).map(UUID::fromString),
                        rs.getInt("product_item_quantity"),
                        rs.getInt("topping_item_per_product_item_quantity"),
                        rs.getBigDecimal("product_price"),
                        rs.getBigDecimal("topping_price")
                )
        );
    }


}
