package com.coffee.order;

import com.coffee.cart.CartItemService;
import com.coffee.cart.DiscountService;
import com.coffee.order.entity.InternalOrderStatus;
import com.coffee.order.entity.database.CustomerOrderEntity;
import com.coffee.publicapi.ExternalCartItemResponse;
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
    @Autowired
    private CartItemService cartItemService;

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

        // Would be good to have an intermediate object here, not use external
        ExternalCartItemResponse externalCartItemResponse = cartItemService.getCartItems(userUid);
        orderProductItemRepository.writeOrderProductItemsFromCart(userUid, orderUid, now, now);
        orderToppingItemRepository.writeOrderToppingItemsFromCart(userUid, now, now);
        // wipe cart
        cartItemService.clearCart(userUid);
        return new ExternalOrderPlacementResponse(
                orderUid,
                discountResponse.originalPrice(),
                discountResponse.finalPrice()
        );
    }
}
