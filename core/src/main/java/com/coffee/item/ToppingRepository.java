package com.coffee.item;

import com.coffee.item.entity.InternalItemStatus;
import com.coffee.item.entity.ToppingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ToppingRepository extends JpaRepository<ToppingEntity, Integer> {

    Optional<ToppingEntity> findByUid(UUID toppingUid);

    List<ToppingEntity> findAllByOrderByCreatedAtDesc();

    List<ToppingEntity> findAllByStatusIs(InternalItemStatus status);

}