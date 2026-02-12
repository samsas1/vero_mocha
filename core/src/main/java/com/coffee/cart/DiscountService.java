package com.coffee.cart;

import com.coffee.cart.entity.CartItemList;
import com.coffee.publicapi.ExternalDiscountResponse;
import com.coffee.publicapi.ExternalDiscountType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.coffee.cart.CartPriceCalculationService.getTotalCartPrice;

@Service
@Transactional
public class DiscountService {

    private static final Logger log = LoggerFactory.getLogger(DiscountService.class);
    @Autowired
    private final CartItemService cartItemService;

    @Autowired
    private final List<DiscountHandler> discountHandlers;


    public DiscountService(
            CartItemService cartItemService,
            List<DiscountHandler> discountHandlers) {
        this.cartItemService = cartItemService;
        this.discountHandlers = discountHandlers;
    }

    public ExternalDiscountResponse getCartDiscount(UUID userUid) {
        CartItemList cartItemList = cartItemService.getCartItemList(userUid);
        BigDecimal originalPrice = getTotalCartPrice(cartItemList);

        // Get all applicable discounts
        List<ExternalDiscountResponse> applicableDiscounts = discountHandlers.stream()
                .map(o -> o.handle(cartItemList))
                .flatMap(Optional::stream).toList();

        log.debug("Found applicable discounts: {} for user: {}", applicableDiscounts, userUid);

        // Return smallest discount or return NO_DISCOUNT response
        return applicableDiscounts.stream()
                .min(Comparator.comparing(ExternalDiscountResponse::finalPrice))
                .orElse(new ExternalDiscountResponse(
                        ExternalDiscountType.NO_DISCOUNT,
                        originalPrice,
                        originalPrice)
                );
    }


}
