package com.coffee.order.custom.query.batch;


import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OrderToppingItemBatchRepository {

    void saveOrderTopping(List<OrderToppingItem> cartToppingItems);

    record OrderToppingItem(
            UUID uid,
            UUID orderProductItemUid,
            UUID toppingUid,
            int quantity,
            Instant createdAt,
            Instant updatedAt
    ) {

    }
}
