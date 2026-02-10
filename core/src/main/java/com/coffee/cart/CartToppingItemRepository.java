package com.coffee.cart;

import com.coffee.cart.entity.CartToppingItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface CartToppingItemRepository extends JpaRepository<CartToppingItemEntity, Integer> {

    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO cart_topping_item (uid, cart_product_item_sid, topping_sid, quantity, created_at)
                         VALUES (
                                     :uid,
                                     (SELECT sid FROM cart_product_item WHERE uid = :cart_product_item_uid),
                                     (SELECT sid FROM topping WHERE uid = :toppingUid),
                                     :quantity,
                                     :createdAt
                         )
            """
    )
    void saveCartProduct(
            UUID uid,
            UUID cart_product_item_uid,
            UUID toppingUid,
            int quantity,
            Instant createdAt
    );

    Optional<CartToppingItemEntity> findByUid(UUID uid);
}
