package com.coffee.order;

import com.coffee.order.entity.CartItemMap;
import com.coffee.order.entity.CartProductItemWithQuantity;
import com.coffee.order.entity.CartToppingItemWithQuantity;
import com.coffee.publicapi.ExternalDiscountResponse;
import com.coffee.publicapi.ExternalDiscountType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FreeItemLargeOrderDiscountHandler implements DiscountHandler {

    private static final Logger log = LoggerFactory.getLogger(FreeItemLargeOrderDiscountHandler.class);

    // TODO add to config so this value can be in the constructor and tested properly
    private final int largeOrderProductCountThreshold = 3;


    public Optional<ExternalDiscountResponse> handle(CartItemMap cartItemMap) {
        if (!discountApplies(cartItemMap)) {
            log.debug("Discount does not apply to cart");
            return Optional.empty();
        }

        List<ProductAndToppingItemTotal> productAndToppingItemTotals = getProductItemPriceIncludingToppings(cartItemMap);
        log.debug("ProductAndToppingTotals: {}", productAndToppingItemTotals);

        ProductAndToppingItemTotal cheapestProductItem = findCheapestItem(productAndToppingItemTotals);
        log.debug("Cheapest product item total object: {}", cheapestProductItem);

        // TODO extract original price into call to not recompute
        BigDecimal originalPrice = cartItemMap.getTotalOriginalPrice();
        BigDecimal finalPrice = originalPrice.subtract(cheapestProductItem.priceIncludingToppings());

        return Optional.of(new ExternalDiscountResponse(
                ExternalDiscountType.FREE_ITEM_FOR_LARGE_ORDER,
                originalPrice,
                finalPrice
        ));
    }

    // TODO handle equally priced items to return consistent product item for discount
    private ProductAndToppingItemTotal findCheapestItem(List<ProductAndToppingItemTotal> productAndToppingItemTotals) {
        return productAndToppingItemTotals.stream()
                .min(Comparator.comparing(ProductAndToppingItemTotal::priceIncludingToppings))
                .orElseThrow();
    }

    private boolean discountApplies(CartItemMap cartItemMap) {
        return !cartItemMap.productsToToppings().isEmpty() &&
                cartItemMap.productsToToppings().size() >= largeOrderProductCountThreshold;
    }

    private List<ProductAndToppingItemTotal> getProductItemPriceIncludingToppings(CartItemMap cartItemMap) {
        return cartItemMap
                .productsToToppings().entrySet()
                .stream()
                .map(entry -> {
                    // Get product item object
                    CartProductItemWithQuantity productItemWithQuantity = entry.getKey();
                    // Calculate total price for the quantity of product
                    BigDecimal totalPriceForProduct = productItemWithQuantity.getPriceForQuantity();
                    // Get topping item objects associated with product
                    List<CartToppingItemWithQuantity> cartToppingItemsWithQuantity = entry.getValue();
                    //Calculate total price for the quantity of topping
                    BigDecimal totalPriceForProductToppings = cartToppingItemsWithQuantity.stream()
                            // Get price for distinct topping item and its quantity
                            .map(CartToppingItemWithQuantity::getPriceForQuantity)
                            //Sum across topping items
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalPrice = totalPriceForProduct.add(totalPriceForProductToppings);
                    return new ProductAndToppingItemTotal(productItemWithQuantity.productItemUid(), totalPrice);

                })
                .toList();
    }

    private record ProductAndToppingItemTotal(
            UUID productItemUid,
            BigDecimal priceIncludingToppings
    ) {
    }
}
