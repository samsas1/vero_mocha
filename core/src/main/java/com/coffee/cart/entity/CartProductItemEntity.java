package com.coffee.cart.entity;

import com.coffee.item.entity.ProductEntity;
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
    @ManyToOne(optional = false)
    private CartEntity cart;
    @ManyToOne(optional = false)
    private ProductEntity product;
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

    public Instant getCreatedAt() {
        return createdAt;
    }
}
