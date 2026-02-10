package com.coffee.order.entity;

import com.coffee.item.entity.ProductEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customer_order_product_item")
public class OrderProductItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sid;
    @Column(nullable = false)
    private UUID uid;
    @ManyToOne(optional = false)
    private OrderEntity order;
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
}
