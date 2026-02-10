package com.coffee.cart.entity;

import com.coffee.item.entity.ToppingEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cart_topping_item")
public class CartToppingItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sid;
    @Column(nullable = false)
    private UUID uid;
    @ManyToOne(optional = false)
    private CartProductItemEntity cartProductItem;
    @ManyToOne(optional = false)
    private ToppingEntity topping;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false)
    private Instant createdAt;

    public UUID getUid() {
        return uid;
    }

    public CartProductItemEntity getCartProductItem() {
        return cartProductItem;
    }

    public ToppingEntity getTopping() {
        return topping;
    }

    public int getQuantity() {
        return quantity;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
