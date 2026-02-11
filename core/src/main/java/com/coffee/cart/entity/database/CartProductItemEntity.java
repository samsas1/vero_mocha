package com.coffee.cart.entity.database;

import com.coffee.item.entity.database.ProductEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cart_product_item")
public class CartProductItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sid;
    @Column(nullable = false)
    private UUID uid;
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private CartEntity cart;
    @ManyToOne(optional = false)
    private ProductEntity product;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false)
    private Instant createdAt;

    public UUID getUid() {
        return uid;
    }

    public CartEntity getCart() {
        return cart;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
