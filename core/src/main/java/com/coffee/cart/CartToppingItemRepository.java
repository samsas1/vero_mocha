package com.coffee.cart;

import com.coffee.cart.batch.CartToppingItemBatchRepository;
import com.coffee.cart.entity.CartToppingItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartToppingItemRepository extends JpaRepository<CartToppingItemEntity, Integer>, CartToppingItemBatchRepository {

    Optional<CartToppingItemEntity> findByUid(UUID uid);


}
