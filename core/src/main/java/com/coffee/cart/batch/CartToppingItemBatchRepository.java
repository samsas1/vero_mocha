package com.coffee.cart.batch;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CartToppingItemBatchRepository {

    void saveCartProduct(List<CartTopping> cartToppings);

    record CartTopping(
            UUID uid,
            UUID cartProductItemUid,
            UUID toppingUid,
            int quantity,
            Instant createdAt
    ) {


    }

}
