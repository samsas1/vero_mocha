package com.coffee.item.entity.database;

import com.coffee.admin.ProductRequest;
import com.coffee.admin.ProductResponse;
import com.coffee.item.entity.InternalItemStatus;
import com.coffee.publicapi.ExternalProductResponse;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.coffee.item.entity.InternalItemStatus.ACTIVE;

/**
 * Product represents a product that can be ordered by customers.
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
        entity.status = Optional.ofNullable(product.itemStatus())
                .map(InternalItemStatus::fromExternal)
                .orElse(ACTIVE);
        // TODO: use time provider for timestamps
        entity.createdAt = Instant.now();
        entity.updatedAt = Instant.now();
        return entity;
    }

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public InternalItemStatus getStatus() {
        return status;
    }

    public void setStatus(InternalItemStatus status) {
        this.status = status;
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

    public ProductResponse toExternalAdmin() {
        return new ProductResponse(
                uid,
                name,
                price,
                status.toExternal(),
                createdAt,
                updatedAt
        );
    }

    public ExternalProductResponse toExternalPublic() {
        return new ExternalProductResponse(
                uid,
                name,
                price,
                createdAt,
                updatedAt
        );
    }
}
