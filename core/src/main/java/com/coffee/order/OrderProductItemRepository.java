package com.coffee.order;

import com.coffee.order.entity.CustomerOrderProductItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductItemRepository extends JpaRepository<CustomerOrderProductItemEntity, Integer> {
}
