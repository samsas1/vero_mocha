package com.coffee.order.entity.database;

import com.coffee.item.entity.database.ProductEntity;
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

    public void setUid(UUID uid) {
        this.uid = uid;
    }

    public CustomerOrderEntity getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(CustomerOrderEntity customerOrder) {
        this.customerOrder = customerOrder;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public BigDecimal getOriginalPricePerProduct() {
        return originalPricePerProduct;
    }

    public void setOriginalPricePerProduct(BigDecimal originalPricePerProduct) {
        this.originalPricePerProduct = originalPricePerProduct;
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
