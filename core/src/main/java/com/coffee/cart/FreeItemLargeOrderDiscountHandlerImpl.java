package com.coffee.cart;

import com.coffee.cart.entity.CartItemList;
import com.coffee.cart.entity.CartProductItem;
import com.coffee.cart.entity.CartToppingItem;
import com.coffee.publicapi.ExternalDiscountResponse;
import com.coffee.publicapi.ExternalDiscountType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.coffee.cart.CartPriceCalculationService.getTotalCartPrice;

@Component
public class FreeItemLargeOrderDiscountHandlerImpl implements DiscountHandler {

    private static final Logger log = LoggerFactory.getLogger(FreeItemLargeOrderDiscountHandlerImpl.class);

    // TODO add to config so this value can be in the constructor and tested properly
    private final int largeOrderProductCountThreshold = 3;


    public Optional<ExternalDiscountResponse> handle(CartItemList cartItemList) {
        if (!discountApplies(cartItemList)) {
            log.debug("Discount does not apply to cart in FreeItemLargeOrderDiscountHandlerImpl");
            return Optional.empty();
        }

        List<ProductAndToppingItemTotal> productAndToppingItemTotals = getProductItemPriceIncludingToppings(cartItemList);
        log.debug("ProductAndToppingTotals in FreeItemLargeOrderDiscountHandlerImpl: {}", productAndToppingItemTotals);

        ProductAndToppingItemTotal cheapestProductItem = findCheapestItem(productAndToppingItemTotals);
        log.debug("Cheapest product item in FreeItemLargeOrderDiscountHandlerImpl: {}", cheapestProductItem);

        // TODO extract original price into call to not recompute
        BigDecimal originalPrice = getTotalCartPrice(cartItemList);
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

    private boolean discountApplies(CartItemList cartItemList) {
        return !cartItemList.cartItems().isEmpty() &&
                cartItemList.cartItems().size() >= largeOrderProductCountThreshold;
    }

    private List<ProductAndToppingItemTotal> getProductItemPriceIncludingToppings(CartItemList cartItemList) {
        return cartItemList
                .cartItems()
                .stream()
                .map(cartItem -> {
                    // Get product item object
                    CartProductItem cartProductItem = cartItem.cartProductItem();
                    // Calculate total price for the quantity of product
                    BigDecimal totalPriceForProduct = cartProductItem.getPriceForQuantity();
                    // Get topping item objects associated with product
                    List<CartToppingItem> cartToppingItemsWithQuantity = cartItem.cartToppingItemList();
                    //Calculate total price for the quantity of topping
                    BigDecimal totalPriceForProductToppings = cartToppingItemsWithQuantity.stream()
                            // Get price for distinct topping item and its quantity
                            .map(CartToppingItem::getPriceForQuantity)
                            //Sum across topping items
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalPrice = totalPriceForProduct.add(totalPriceForProductToppings);
                    return new ProductAndToppingItemTotal(cartProductItem.productItemUid(), totalPrice);

                })
                .toList();
    }

    private record ProductAndToppingItemTotal(
            UUID productItemUid,
            BigDecimal priceIncludingToppings
    ) {
    }
}
