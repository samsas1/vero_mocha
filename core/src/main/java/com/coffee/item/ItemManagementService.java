package com.coffee.item;

import com.coffee.admin.*;
import com.coffee.exception.ResourceNotFoundException;
import com.coffee.item.entity.database.ProductEntity;
import com.coffee.item.entity.database.ToppingEntity;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.coffee.item.entity.InternalItemStatus.fromExternal;

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
                .stream().map(this::map).toList();
        return new ToppingResponseList(toppings);
    }

    public ToppingResponse getTopping(UUID uid) {
        return toppingRepository.findByUid(uid)
                .map(this::map)
                .orElseThrow(() -> new ResourceNotFoundException("Topping", "uid", uid));
    }

    public ToppingResponse saveTopping(ToppingRequest topping) {
        return map(toppingRepository.save(ToppingEntity.fromExternal(topping)));
    }


    public ToppingResponse updateTopping(UUID toppingUid, ToppingRequest toppingRequest) {
        Instant instant = Instant.now();
        ToppingEntity topping = toppingRepository.findByUid(toppingUid)
                .orElseThrow(() -> new ResourceNotFoundException("Topping", "uid", toppingUid));
        topping.setName(toppingRequest.name());
        topping.setPrice(toppingRequest.price());
        Optional.ofNullable(toppingRequest.itemStatus())
                .ifPresent(status -> topping.setStatus(fromExternal(status)));
        topping.setUpdatedAt(instant);
        return map(topping);
    }

    public void deleteTopping(UUID toppingUid) {
        ToppingEntity topping = toppingRepository.findByUid(toppingUid)
                .orElseThrow(() -> new ResourceNotFoundException("Topping", "uid", toppingUid));
        if (toppingRepository.existsLinkedToCartOrOrder(toppingUid)) {
            throw new IllegalStateException("Cannot delete topping linked to cart or order");
        }
        toppingRepository.delete(topping);
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

    public ProductResponse saveProduct(ProductRequest topping) {
        return map(productRepository.save(ProductEntity.fromExternal(topping)));
    }

    public ProductResponse updateProduct(UUID productUid, ProductRequest productRequest) {
        Instant instant = Instant.now();
        ProductEntity product = productRepository.findByUid(productUid)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "uid", productUid));
        product.setName(productRequest.name());
        product.setPrice(productRequest.price());
        Optional.ofNullable(productRequest.itemStatus())
                .ifPresent(status -> product.setStatus(fromExternal(status)));
        product.setUpdatedAt(instant);
        return map(product);
    }

    public void deleteProduct(UUID productUid) {
        ProductEntity product = productRepository.findByUid(productUid)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "uid", productUid));
        if (productRepository.existsLinkedToCartOrOrder(productUid)) {
            throw new IllegalStateException("Cannot delete product linked to cart or order");
        }
        productRepository.delete(product);
    }


    private ToppingResponse map(ToppingEntity topping) {
        return topping.toExternalAdmin();
    }

    private ProductResponse map(ProductEntity product) {
        return product.toExternalAdmin();
    }


}
