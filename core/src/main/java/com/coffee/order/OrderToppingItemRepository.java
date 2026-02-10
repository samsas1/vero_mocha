package com.coffee.order;

import com.coffee.order.custom.query.batch.OrderToppingItemBatchRepository;
import com.coffee.order.entity.database.CustomerOrderToppingItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderToppingItemRepository extends
        JpaRepository<CustomerOrderToppingItemEntity, Integer>,
        OrderToppingItemBatchRepository {
}
