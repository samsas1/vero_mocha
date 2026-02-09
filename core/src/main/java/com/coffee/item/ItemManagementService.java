package com.coffee.item;

import com.coffee.admin.ProductCreationRequest;
import com.coffee.admin.ProductResponse;
import com.coffee.admin.ToppingCreationRequest;
import com.coffee.admin.ToppingResponse;
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

    public List<ToppingResponse> listToppings() {
        return toppingRepository.findAllByOrderByCreatedAtDesc().stream().map(ToppingEntity::toExternal).toList();
    }

    public ToppingResponse getTopping(UUID uid) {
        return toppingRepository.findByUid(uid)
                .map(ToppingEntity::toExternal)
                .orElseThrow(() -> new ResourceNotFoundException("Topping", "uid", uid));
    }

    public UUID saveTopping(ToppingCreationRequest topping) {
        return toppingRepository.save(ToppingEntity.fromExternal(topping)).getUid();
    }

    public List<ProductResponse> listProducts() {
        return productRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(ProductEntity::toExternal).toList();
    }

    public ProductResponse getProduct(UUID uid) {
        return productRepository.findByUid(uid)
                .map(ProductEntity::toExternal)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "uid", uid));
    }

    public UUID saveProduct(ProductCreationRequest topping) {
        return productRepository.save(ProductEntity.fromExternal(topping)).getUid();
    }


}
