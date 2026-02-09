package com.coffee.item.entity;

import com.coffee.admin.ToppingRequest;
import com.coffee.admin.ToppingResponse;
import com.coffee.publicapi.ExternalToppingResponse;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Topping entity representing a topping that can be added to a product.
 */
@Entity
@Table(name = "topping")
public class ToppingEntity {

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

    public static ToppingEntity fromExternal(ToppingRequest topping) {
        ToppingEntity entity = new ToppingEntity();
        entity.uid = UUID.randomUUID();
        entity.name = topping.name();
        entity.price = topping.price();
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

    public ToppingResponse toExternalAdmin() {
        return new ToppingResponse(
                uid,
                name,
                price,
                status.toExternal(),
                createdAt,
                updatedAt
        );
    }

    public ExternalToppingResponse toExternalPublic() {
        return new ExternalToppingResponse(
                uid,
                name,
                price,
                createdAt,
                updatedAt
        );
    }
}
