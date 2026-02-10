package com.coffee.order;

import com.coffee.order.entity.CartItemMap;
import com.coffee.publicapi.ExternalDiscountResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static com.coffee.publicapi.ExternalDiscountType.FULL_CART;

public class FullCartDiscountHandler implements DiscountHandler {

    private static final Logger log = LoggerFactory.getLogger(FullCartDiscountHandler.class);

    // TODO add to config
    private final BigDecimal discountMultiplier = BigDecimal.valueOf(0.75);
    private final BigDecimal discountThreshold = BigDecimal.valueOf(12);

    public Optional<ExternalDiscountResult> handle(CartItemMap cartItemMap) {
        if (cartItemMap.productsToToppings().isEmpty()) {
            log.debug("No product items found in cart");
            return Optional.empty();
        }

        BigDecimal originalPrice = getTotalOriginalPrice(cartItemMap);

        if (originalPrice.compareTo(discountThreshold) < 0) {
            log.debug("Discount threshold not met");
            return Optional.empty();
        }
        // Floor final price to avoid customer dissatisfaction from rounding fractional cents up
        BigDecimal finalPrice = originalPrice.multiply(discountMultiplier).setScale(2, RoundingMode.FLOOR);
        return Optional.of(
                new ExternalDiscountResult(
                        FULL_CART,
                        originalPrice,
                        finalPrice,
                        null
                )
        );
    }
}
