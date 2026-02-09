package com.coffee.item;

import com.coffee.item.entity.InternalItemStatus;
import com.coffee.item.entity.ProductEntity;
import com.coffee.item.entity.ToppingEntity;
import com.coffee.publicapi.ExternalProductResponse;
import com.coffee.publicapi.ExternalProductResponseList;
import com.coffee.publicapi.ExternalToppingResponse;
import com.coffee.publicapi.ExternalToppingResponseList;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ItemBrowsingService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ToppingRepository toppingRepository;

    public ItemBrowsingService(ProductRepository productRepository, ToppingRepository toppingRepository) {
        this.productRepository = productRepository;
        this.toppingRepository = toppingRepository;
    }

    public ExternalToppingResponseList listActiveToppings() {
        List<ExternalToppingResponse> toppings = toppingRepository.findAllByStatusIs(InternalItemStatus.ACTIVE)
                .stream().map(ToppingEntity::toExternalPublic).toList();
        return new ExternalToppingResponseList(toppings);
    }

    public ExternalProductResponseList listActiveProducts() {
        List<ExternalProductResponse> products = productRepository.findAllByStatusIs(InternalItemStatus.ACTIVE)
                .stream().map(ProductEntity::toExternalPublic).toList();
        return new ExternalProductResponseList(products);
    }


}
