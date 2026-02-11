package com.coffee.item;

import com.coffee.item.entity.InternalItemStatus;
import com.coffee.item.entity.database.ToppingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ToppingRepository extends JpaRepository<ToppingEntity, Integer> {


    Optional<ToppingEntity> findByUid(UUID toppingUid);

    List<ToppingEntity> findAllByOrderByCreatedAtDesc();

    List<ToppingEntity> findAllByStatusIs(InternalItemStatus status);

    @Query(nativeQuery = true, value = """
            SELECT EXISTS (
                SELECT 1
                FROM cart_topping_item cti
                JOIN topping t ON t.sid = cti.topping_sid
                WHERE t.uid = :toppingUid
            ) OR EXISTS (
                SELECT 1
                FROM customer_order_topping_item coti
                JOIN topping t ON t.sid = coti.topping_sid
                WHERE t.uid = :toppingUid
            )
            """)
    boolean existsLinkedToCartOrOrder(@Param("toppingUid") UUID toppingUid);

}