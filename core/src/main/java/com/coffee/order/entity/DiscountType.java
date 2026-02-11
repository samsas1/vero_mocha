package com.coffee.order.entity;

import com.coffee.publicapi.ExternalDiscountType;

public enum DiscountType {

    NO_DISCOUNT,
    FULL_CART,
    FREE_ITEM_FOR_LARGE_ORDER;

    public static DiscountType fromExternal(ExternalDiscountType discountType) {
        switch (discountType) {
            case NO_DISCOUNT:
                return NO_DISCOUNT;
            case FULL_CART:
                return FULL_CART;
            case FREE_ITEM_FOR_LARGE_ORDER:
                return FREE_ITEM_FOR_LARGE_ORDER;
            default:
                throw new IllegalArgumentException("Unknown discount type: " + discountType);
        }
    }

    public ExternalDiscountType toExternal() {
        switch (this) {
            case NO_DISCOUNT:
                return ExternalDiscountType.NO_DISCOUNT;
            case FULL_CART:
                return ExternalDiscountType.FULL_CART;
            case FREE_ITEM_FOR_LARGE_ORDER:
                return ExternalDiscountType.FREE_ITEM_FOR_LARGE_ORDER;
            default:
                throw new IllegalArgumentException("Unknown discount type: " + this);
        }
    }


}
