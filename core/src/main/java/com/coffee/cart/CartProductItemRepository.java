package com.coffee.cart;

import com.coffee.cart.entity.database.CartProductItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartProductItemRepository extends JpaRepository<CartProductItemEntity, Integer> {

    /**
     * This query persists the cart product item which requires the cart and product foreign keys.
     * Product key is resolved via UUID associated with the product.
     * The cart is resolved via the unique user UUID associated with the cart.
     *
     * @param uid
     * @param userUid
     * @param productUid
     * @param quantity
     * @param createdAt
     */
    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO cart_product_item (uid, cart_sid, product_sid, quantity, created_at)
                         VALUES (
                                     :uid,
                                     (SELECT sid FROM cart WHERE user_uid = :userUid),
                                     (SELECT sid FROM product WHERE uid = :productUid),
                                     :quantity,
                                     :createdAt
                         )
            """
    )
    void saveCartProduct(
            UUID uid,
            UUID userUid,
            UUID productUid,
            int quantity,
            Instant createdAt
    );

    Optional<CartProductItemEntity> findByUid(UUID uid);

    List<CartProductItemEntity> getCartProductItemEntitiesByCart_UserUid(UUID userUid);

    /**
     * Deletes cart items for a given user.
     * The query deletes product items, but the deletion is cascaded to the topping items via the foreign key constraint with cascade delete.
     *
     * @param userUid
     */
    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM cart_product_item
            WHERE cart_sid = (SELECT sid FROM cart WHERE user_uid = :userUid)
            """)
    void deleteCartItems(UUID userUid);

    @Query(nativeQuery = true, value = """
            SELECT CASE WHEN COUNT(*) = 0 THEN TRUE ELSE FALSE END
            FROM cart_product_item
            WHERE cart_sid = (SELECT sid FROM cart WHERE user_uid = :userUid)
            """)
    boolean isCartEmpty(UUID userUid);
}
