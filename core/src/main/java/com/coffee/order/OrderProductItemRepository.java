package com.coffee.order;

import com.coffee.order.entity.database.CustomerOrderProductItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


public interface OrderProductItemRepository extends JpaRepository<CustomerOrderProductItemEntity, Integer> {

    @Modifying
    @Query(nativeQuery = true, value = """
            WITH cart_product_item_fields AS (
                                    SELECT cpi.uid AS product_item_uid,
                                           p.sid AS product_sid,
                                           p.price AS product_price,
                                           cpi.quantity AS product_item_quantity
                                    FROM cart c
                                    JOIN cart_product_item cpi ON cpi.cart_sid = c.sid
                                    JOIN product p ON cpi.product_sid = p.sid
                                    WHERE c.user_uid = :userUid
                                )
                                INSERT INTO customer_order_product_item (
                                                                         uid, 
                                                                         customer_order_sid,
                                                                         product_sid, 
                                                                         original_price_per_product, 
                                                                         quantity,
                                                                         created_at,
                                                                         updated_at
                                                                         ) 
                                SELECT product_item_uid,
                                       (SELECT sid FROM customer_order WHERE uid = :orderUid)
                                       product_sid,
                                       product_price,
                                       product_item_quantity,
                                       :createdAt,
                                       :updatedAt
                                FROM cart_product_item_fields
            """)
    void writeOrderProductItemsFromCart(UUID userUid, UUID orderUid, Instant createdAt, Instant updatedAt);

    List<CustomerOrderProductItemEntity> getCustomerOrderProductItemEntitiesByCustomerOrder_UserUid(UUID userUid);
}
