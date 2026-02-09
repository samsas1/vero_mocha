package com.coffee.item.entity;

import com.coffee.enumerators.ItemStatus;

/**
 * InternalItemStatus is an enum that represents the availability of an item.
 * When ACTIVE, it can be ordered by customers. When INACTIVE, it is only maintained for past orders.
 */
public enum InternalItemStatus {

    ACTIVE,
    INACTIVE;

    public static InternalItemStatus fromExternal(ItemStatus externalStatus) {
        switch (externalStatus) {
            case ACTIVE:
                return ACTIVE;
            case INACTIVE:
                return INACTIVE;
            default:
                throw new IllegalArgumentException("Unknown external status: " + externalStatus);
        }
    }

    public ItemStatus toExternal() {
        switch (this) {
            case ACTIVE:
                return ItemStatus.ACTIVE;
            case INACTIVE:
                return ItemStatus.INACTIVE;
            default:
                throw new IllegalArgumentException("Unknown internal status: " + this);
        }
    }
}
