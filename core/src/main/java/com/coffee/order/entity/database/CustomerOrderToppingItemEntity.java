package com.coffee.order.entity.database;

import com.coffee.item.entity.database.ToppingEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customer_order_topping_item")
public class CustomerOrderToppingItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sid;
    @Column(nullable = false)
    private UUID uid;
    @ManyToOne(optional = false)
    private CustomerOrderProductItemEntity customerOrderProductItem;
    @ManyToOne(optional = false)
    private ToppingEntity topping;
    @Column(nullable = false)
    private BigDecimal originalPricePerTopping;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;

    public UUID getUid() {
        return uid;
    }

    public CustomerOrderProductItemEntity getCustomerOrderProductItem() {
        return customerOrderProductItem;
    }

    public ToppingEntity getTopping() {
        return topping;
    }

    public BigDecimal getOriginalPricePerTopping() {
        return originalPricePerTopping;
    }

    public int getQuantity() {
        return quantity;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
