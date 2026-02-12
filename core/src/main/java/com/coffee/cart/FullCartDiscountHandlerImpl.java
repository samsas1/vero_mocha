package com.coffee.cart;

import com.coffee.cart.entity.CartItemList;
import com.coffee.publicapi.ExternalDiscountResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static com.coffee.cart.CartPriceCalculationService.getTotalCartPrice;
import static com.coffee.publicapi.ExternalDiscountType.FULL_CART;

@Component
public class FullCartDiscountHandlerImpl implements DiscountHandler {

    private static final Logger log = LoggerFactory.getLogger(FullCartDiscountHandlerImpl.class);

    // TODO add to config and have the amount configured in the constructor
    private final BigDecimal discountMultiplier = BigDecimal.valueOf(0.75);
    private final BigDecimal discountThreshold = BigDecimal.valueOf(12);

    public Optional<ExternalDiscountResponse> handle(CartItemList cartItemList) {
        if (cartItemList.cartItems().isEmpty()) {
            log.debug("No product items found in cart in FullCartDiscountHandlerImpl");
            return Optional.empty();
        }
        // TODO extract original price into call to not recompute
        BigDecimal originalPrice = getTotalCartPrice(cartItemList);

        if (originalPrice.compareTo(discountThreshold) < 0) {
            log.debug("Discount threshold not met in FullCartDiscountHandlerImpl");
            return Optional.empty();
        }
        // Floor final price to avoid customer dissatisfaction from rounding fractional cents up
        BigDecimal finalPrice = originalPrice.multiply(discountMultiplier).setScale(2, RoundingMode.FLOOR);
        return Optional.of(
                new ExternalDiscountResponse(
                        FULL_CART,
                        originalPrice,
                        finalPrice
                )
        );
    }
}
