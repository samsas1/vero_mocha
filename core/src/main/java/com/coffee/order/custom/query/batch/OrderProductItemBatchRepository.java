package com.coffee.order.custom.query.batch;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OrderProductItemBatchRepository {


    void saveOrderProduct(List<OrderProductItem> cartToppingItems);

    record OrderProductItem(
            UUID uid,
            UUID orderUid,
            UUID productUid,
            int quantity,
            Instant createdAt,
            Instant updatedAt
    ) {

    }
}
