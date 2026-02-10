package com.coffee.order;

import com.coffee.order.custom.query.CartFinalizationRepository;
import com.coffee.order.entity.CartItemMap;
import com.coffee.order.entity.CartProductItemWithQuantity;
import com.coffee.order.entity.CartToppingItemWithQuantity;
import com.coffee.order.entity.database.CartItemTableEntryEntity;
import com.coffee.publicapi.ExternalDiscountResponse;
import com.coffee.publicapi.ExternalDiscountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public ExternalDiscountResponse checkCartDiscount(UUID userUid) {
        List<CartItemTableEntryEntity> cartTotals = cartFinalizationRepository.listCartItemTable(userUid);
        // Collect a list of cart items which duplicates product items in favor of toppings into a map with unique
        // product item keys
        CartItemMap cartItemMap = collectItemTableIntoMap(cartTotals);
        BigDecimal originalPrice = cartItemMap.getTotalOriginalPrice();

        // Get all applicable discounts
        Stream<ExternalDiscountResponse> applicableDiscounts = discountHandlers.stream()
                .map(o -> o.handle(cartItemMap))
                .flatMap(Optional::stream);

        // Return smallest discount or return NO discount response
        return applicableDiscounts
                .min(Comparator.comparing(ExternalDiscountResponse::finalPrice))
                .orElse(new ExternalDiscountResponse(
                        ExternalDiscountType.NO_DISCOUNT,
                        originalPrice,
                        originalPrice)
                );

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
