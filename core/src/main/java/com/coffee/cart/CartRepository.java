package com.coffee.cart;

import com.coffee.cart.entity.database.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<CartEntity, Integer> {
}
