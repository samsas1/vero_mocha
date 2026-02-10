package com.coffee.order.entity.database;

import com.coffee.item.entity.ProductEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customer_order_product_item")
public class CustomerOrderProductItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sid;
    @Column(nullable = false)
    private UUID uid;
    @ManyToOne(optional = false)
    private CustomerOrderEntity customerOrder;
    @ManyToOne(optional = false)
    private ProductEntity product;
    @Column(nullable = false)
    private BigDecimal originalPricePerProduct;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;

    public UUID getUid() {
        return uid;
    }

    public CustomerOrderEntity getCustomerOrder() {
        return customerOrder;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public BigDecimal getOriginalPricePerProduct() {
        return originalPricePerProduct;
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
