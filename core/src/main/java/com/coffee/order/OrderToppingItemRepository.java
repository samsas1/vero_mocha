package com.coffee.order;

import com.coffee.order.entity.database.CustomerOrderProductItemEntity;
import com.coffee.order.entity.database.CustomerOrderToppingItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OrderToppingItemRepository extends JpaRepository<CustomerOrderToppingItemEntity, Integer> {

    /**
     * This query copies the topping items in cart into ordered topping items and links them to an already created order.
     * As the query does not delete the cart topping items, this needs to be done downstream.
     *
     * @param userUid
     * @param createdAt
     * @param updatedAt
     */
    @Modifying
    @Query(nativeQuery = true, value = """
            WITH cart_topping_item_fields AS (
                                    SELECT cti.uid AS topping_item_uid,
                                           cpi.uid AS product_item_uid,
                                           t.sid AS topping_sid,
                                           t.price AS topping_price,
                                           cti.quantity AS topping_item_quantity
                                    FROM cart c
                                    JOIN cart_product_item cpi ON cpi.cart_sid = c.sid
                                    JOIN cart_topping_item cti ON cti.cart_product_item_sid = cpi.sid
                                    JOIN topping t ON cti.topping_sid = t.sid
                                    WHERE c.user_uid = :userUid
                                )
                                INSERT INTO customer_order_topping_item (
                                                                         uid,
                                                                         customer_order_product_item_sid,
                                                                         topping_sid, 
                                                                         original_price_per_topping, 
                                                                         quantity,
                                                                         created_at,
                                                                         updated_at
                                                                         ) 
                                SELECT topping_item_uid,
                                       (SELECT sid FROM customer_order_product_item WHERE uid = product_item_uid),
                                       topping_sid,
                                       topping_price,
                                       topping_item_quantity,
                                       :createdAt,
                                       :updatedAt
                                FROM cart_topping_item_fields
            """)
    void writeOrderToppingItemsFromCart(UUID userUid, Instant createdAt, Instant updatedAt);

    List<CustomerOrderToppingItemEntity> getOrderToppingItemEntitiesByCustomerOrderProductItemIn(List<CustomerOrderProductItemEntity> productItemEntities);
}
