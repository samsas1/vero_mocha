package com.coffee.cart.custom.query.batch;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CartToppingItemBatchRepository {

    void saveCartProduct(List<CartToppingItemRecord> cartToppingItems);

    record CartToppingItemRecord(
            UUID uid,
            UUID cartProductItemUid,
            UUID toppingUid,
            int quantity,
            Instant createdAt
    ) {


    }

}
