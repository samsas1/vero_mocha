package com.coffee.item;

import com.coffee.item.entity.InternalItemStatus;
import com.coffee.item.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {

    Optional<ProductEntity> findByUid(UUID productUid);

    List<ProductEntity> findAllByOrderByCreatedAtDesc();

    List<ProductEntity> findAllByStatusIs(InternalItemStatus status);
}
