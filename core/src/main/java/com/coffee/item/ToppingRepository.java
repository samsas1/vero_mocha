package com.coffee.item;

import com.coffee.item.entity.Topping;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ToppingRepository extends CrudRepository<Topping, Integer> {

    Optional<Topping> findByUid(UUID toppingUid);
}