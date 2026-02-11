package com.coffee.cart.entity.database;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cart")
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sid;
    @Column(nullable = false)
    private UUID uid;
    @Column(nullable = false)
    private UUID userUid;
    @Column(nullable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;

    public UUID getUid() {
        return uid;
    }

    public UUID getUserUid() {
        return userUid;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
