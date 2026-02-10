package com.coffee.order;

import com.coffee.order.custom.query.batch.OrderProductItemBatchRepository;
import com.coffee.order.entity.CustomerOrderProductItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderProductItemRepository extends
        JpaRepository<CustomerOrderProductItemEntity, Integer>,
        OrderProductItemBatchRepository {
}
