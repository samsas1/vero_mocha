package com.coffee.order.entity;

import com.coffee.order.entity.database.CartItemTableEntryEntity;

import java.util.List;

public record CartTotals(List<CartItemTableEntryEntity> totals) {
}
