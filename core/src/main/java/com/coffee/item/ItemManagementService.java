package com.coffee.item;

import com.coffee.admin.*;
import com.coffee.exception.ResourceNotFoundException;
import com.coffee.item.entity.ProductEntity;
import com.coffee.item.entity.ToppingEntity;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ItemManagementService {

    @Autowired
    ToppingRepository toppingRepository;

    @Autowired
    ProductRepository productRepository;

    public ItemManagementService(ToppingRepository toppingRepository, ProductRepository productRepository) {
        this.toppingRepository = toppingRepository;
        this.productRepository = productRepository;
    }

    public ToppingResponseList listToppings() {
        List<ToppingResponse> toppings = toppingRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(ToppingEntity::toExternalAdmin).toList();
        return new ToppingResponseList(toppings);
    }

    public ToppingResponse getTopping(UUID uid) {
        return toppingRepository.findByUid(uid)
                .map(ToppingEntity::toExternalAdmin)
                .orElseThrow(() -> new ResourceNotFoundException("Topping", "uid", uid));
    }

    public UUID saveTopping(ToppingRequest topping) {
        return toppingRepository.save(ToppingEntity.fromExternal(topping)).getUid();
    }

    public ProductResponseList listProducts() {
        List<ProductResponse> productResponses = productRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(ProductEntity::toExternalAdmin).toList();
        return new ProductResponseList(productResponses);
    }

    public ProductResponse getProduct(UUID uid) {
        return productRepository.findByUid(uid)
                .map(ProductEntity::toExternalAdmin)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "uid", uid));
    }

    public UUID saveProduct(ProductRequest topping) {
        return productRepository.save(ProductEntity.fromExternal(topping)).getUid();
    }


}
