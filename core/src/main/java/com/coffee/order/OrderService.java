package com.coffee.order;

import com.coffee.cart.DiscountService;
import com.coffee.order.entity.InternalOrderStatus;
import com.coffee.order.entity.database.CustomerOrderEntity;
import com.coffee.publicapi.ExternalDiscountResponse;
import com.coffee.publicapi.ExternalOrderPlacementResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    @Autowired
    private final OrderRepository orderRepository;

    @Autowired
    private final OrderProductItemRepository orderProductItemRepository;

    @Autowired
    private final OrderToppingItemRepository orderToppingItemRepository;

    @Autowired
    private final DiscountService discountService;

    public OrderService(
            OrderRepository orderRepository,
            OrderProductItemRepository orderProductItemRepository,
            OrderToppingItemRepository orderToppingItemRepository,
            DiscountService discountService) {
        this.orderRepository = orderRepository;
        this.orderProductItemRepository = orderProductItemRepository;
        this.orderToppingItemRepository = orderToppingItemRepository;
        this.discountService = discountService;
    }


    public ExternalOrderPlacementResponse placeOrder(UUID userUid) {
        UUID orderUid = UUID.randomUUID();
        Instant now = Instant.now();
        ExternalDiscountResponse discountResponse = discountService.checkCartDiscount(userUid);

        CustomerOrderEntity orderEntity = new CustomerOrderEntity();
        orderEntity.setUid(orderUid);
        orderEntity.setUserUid(userUid);
        orderEntity.setOrderStatus(InternalOrderStatus.PLACED);
        orderEntity.setOriginalPrice(discountResponse.originalPrice());
        orderEntity.setFinalPrice(discountResponse.finalPrice());
        orderEntity.setCreatedAt(now);
        orderEntity.setUpdatedAt(now);

        orderRepository.save(orderEntity);

        orderProductItemRepository.saveOrderProduct();
    }
}
