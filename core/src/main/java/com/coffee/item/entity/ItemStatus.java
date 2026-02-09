package com.coffee.item.entity;

/**
 * ItemStatus is an enum that represents the availability of an item.
 * When ACTIVE, it can be ordered by customers. When INACTIVE, it is only maintained for past orders.
 */
public enum ItemStatus {

    ACTIVE,
    INACTIVE
}
