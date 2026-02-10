package com.coffee.cart.batch;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
public class CartToppingItemBatchRepositoryImpl implements CartToppingItemBatchRepository {

    private static final String INSERT_PLACEHOLDER = """
            INSERT INTO cart_topping_item (uid, cart_product_item_sid, topping_sid, quantity, created_at) VALUES 
            """;
    private static final String VALUE_PLACEHOLDER = """
            (?,
            SELECT sid FROM cart_product_item WHERE ? = uid,
            SELECT sid FROM topping WHERE ? = uid,
             ?,
             ?)
            """;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void saveCartProduct(List<CartTopping> cartToppings) {
        if (cartToppings.isEmpty())
            return;

        String sql = buildInsertSql(cartToppings.size());
        Query query = entityManager.createNativeQuery(sql);
        bindParameters(query, cartToppings);
        query.executeUpdate();
    }

    private String buildInsertSql(int count) {
        return INSERT_PLACEHOLDER +
                IntStream.range(0, count)
                        .mapToObj(i -> VALUE_PLACEHOLDER)
                        .collect(Collectors.joining(", "));
    }

    private void bindParameters(Query query, List<CartTopping> toppings) {
        int position = 1;
        for (CartTopping cartTopping : toppings) {
            query.setParameter(position++, cartTopping.uid());
            query.setParameter(position++, cartTopping.cartProductItemUid());
            query.setParameter(position++, cartTopping.toppingUid());
            query.setParameter(position++, cartTopping.quantity());
            query.setParameter(position++, cartTopping.createdAt());
        }

    }
}
