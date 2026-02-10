package com.coffee.order;

import com.coffee.order.entity.CustomerOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<CustomerOrderEntity, Integer> {
}
