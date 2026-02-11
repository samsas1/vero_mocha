package com.coffee.cart;

import com.coffee.cart.entity.CartItemList;
import com.coffee.publicapi.ExternalDiscountResponse;
import com.coffee.publicapi.ExternalDiscountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@Transactional
public class DiscountService {

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

    public ExternalDiscountResponse checkCartDiscount(UUID userUid) {
        CartItemList cartItemList = cartItemService.getCartItemList(userUid);
        BigDecimal originalPrice = cartItemList.getTotalOriginalPrice();

        // Get all applicable discounts
        Stream<ExternalDiscountResponse> applicableDiscounts = discountHandlers.stream()
                .map(o -> o.handle(cartItemList))
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


}
