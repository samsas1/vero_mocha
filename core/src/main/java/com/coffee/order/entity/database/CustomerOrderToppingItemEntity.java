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

    public void setUid(UUID uid) {
        this.uid = uid;
    }

    public CustomerOrderProductItemEntity getCustomerOrderProductItem() {
        return customerOrderProductItem;
    }

    public void setCustomerOrderProductItem(CustomerOrderProductItemEntity customerOrderProductItem) {
        this.customerOrderProductItem = customerOrderProductItem;
    }

    public UUID getProductItemUid() {
        return customerOrderProductItem.getUid();
    }

    public ToppingEntity getTopping() {
        return topping;
    }

    public void setTopping(ToppingEntity topping) {
        this.topping = topping;
    }

    public BigDecimal getOriginalPricePerTopping() {
        return originalPricePerTopping;
    }

    public void setOriginalPricePerTopping(BigDecimal originalPricePerTopping) {
        this.originalPricePerTopping = originalPricePerTopping;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
