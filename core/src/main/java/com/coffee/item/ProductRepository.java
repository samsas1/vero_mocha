package com.coffee.item;

import com.coffee.item.entity.InternalItemStatus;
import com.coffee.item.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {

    Optional<ProductEntity> findByUid(UUID productUid);

    List<ProductEntity> findAllByOrderByCreatedAtDesc();

    List<ProductEntity> findAllByStatusIs(InternalItemStatus status);

    @Query(nativeQuery = true, value = """
            SELECT EXISTS (
                SELECT 1
                FROM cart_product_item cpi
                JOIN product p ON p.sid = cpi.product_sid
                WHERE p.uid = :productUid
            ) OR EXISTS (
                SELECT 1
                FROM customer_order_product_item copi
                JOIN product p ON p.sid = copi.product_sid
                WHERE p.uid = :productUid
            )
            """)
    boolean existsLinkedToCartOrOrder(@Param("productUid") UUID productUid);
}
