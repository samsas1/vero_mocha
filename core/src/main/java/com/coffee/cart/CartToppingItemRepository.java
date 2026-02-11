package com.coffee.cart;

import com.coffee.cart.custom.query.batch.CartToppingItemBatchRepository;
import com.coffee.cart.entity.database.CartProductItemEntity;
import com.coffee.cart.entity.database.CartToppingItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartToppingItemRepository extends JpaRepository<CartToppingItemEntity, Integer>, CartToppingItemBatchRepository {

    Optional<CartToppingItemEntity> findByUid(UUID uid);


    List<CartToppingItemEntity> getCartToppingItemEntitiesByCartProductItemIn(List<CartProductItemEntity> cartProductItemEntities);
}
