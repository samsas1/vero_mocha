package com.coffee.order;

import com.coffee.order.entity.database.CustomerOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<CustomerOrderEntity, Integer> {
}
