package com.coffee.item.entity;

import com.coffee.admin.ProductRequest;
import com.coffee.admin.ProductResponse;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Product represents a product, currently a drink, that can be ordered by customers.
 */
@Entity
@Table(name = "product")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sid;
    @Column(nullable = false)
    private UUID uid;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private BigDecimal price;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InternalItemStatus status;
    @Column(nullable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;

    public static ProductEntity fromExternal(ProductRequest product) {
        ProductEntity entity = new ProductEntity();
        entity.uid = UUID.randomUUID();
        entity.name = product.name();
        entity.price = product.price();
        entity.status = InternalItemStatus.ACTIVE;
        // TODO: use time provider for timestamps
        entity.createdAt = Instant.now();
        entity.updatedAt = Instant.now();
        return entity;
    }

    public UUID getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public InternalItemStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public ProductResponse toExternal() {
        return new ProductResponse(
                uid,
                name,
                price,
                status.toExternal(),
                createdAt,
                updatedAt
        );
    }
}
