package com.coffee.order;

import com.coffee.order.custom.query.CartFinalizationRepository;
import com.coffee.order.entity.CartItemMap;
import com.coffee.order.entity.CartProductItemWithQuantity;
import com.coffee.order.entity.CartToppingItemWithQuantity;
import com.coffee.order.entity.database.CartItemTableEntryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class DiscountService {

    @Autowired
    private final CartFinalizationRepository cartFinalizationRepository;

    @Autowired
    private final List<DiscountHandler> discountHandlers;

    public DiscountService(CartFinalizationRepository cartFinalizationRepository,
                           List<DiscountHandler> discountHandlers) {
        this.cartFinalizationRepository = cartFinalizationRepository;
        this.discountHandlers = discountHandlers;
    }

    public void checkCartDiscount(UUID userUid) {
        List<CartItemTableEntryEntity> cartTotals = cartFinalizationRepository.listCartItemTable(userUid);

        // Collect a list of cart items which duplicates product items in favor of toppings into a map with unique
        // product item keys
        CartItemMap cartItemMap = collectItemTableIntoMap(cartTotals);


    }

    private CartItemMap collectItemTableIntoMap(List<CartItemTableEntryEntity> cartTotals) {
        Map<CartProductItemWithQuantity, List<CartToppingItemWithQuantity>> productsToToppings = cartTotals.stream()
                .collect(Collectors.groupingBy(
                                CartProductItemWithQuantity::fromCartTotalsEntity,
                                Collectors.mapping(
                                        CartToppingItemWithQuantity::fromCartTotalsEntity,
                                        Collectors.toList()
                                )
                        )
                );
        return new CartItemMap(productsToToppings);
    }
}
