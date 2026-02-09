package com.coffee.item;

import com.coffee.item.entity.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends CrudRepository<Product, Integer> {

    Optional<Product> findByUid(UUID productUid);
}
