package com.coffee.order;

import com.coffee.cart.CartItemService;
import com.coffee.cart.DiscountService;
import com.coffee.order.entity.DiscountType;
import com.coffee.order.entity.database.CustomerOrderEntity;
import com.coffee.order.entity.database.CustomerOrderProductItemEntity;
import com.coffee.order.entity.database.CustomerOrderToppingItemEntity;
import com.coffee.publicapi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.coffee.order.entity.InternalOrderStatus.PLACED;
import static java.util.stream.Collectors.groupingBy;

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
        orderEntity.setOrderStatus(PLACED);
        orderEntity.setDiscountType(DiscountType.fromExternal(discountResponse.discountType()));
        orderEntity.setOriginalPrice(discountResponse.originalPrice());
        orderEntity.setFinalPrice(discountResponse.finalPrice());
        orderEntity.setCreatedAt(now);
        orderEntity.setUpdatedAt(now);

        orderRepository.save(orderEntity);

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

    public ExternalOrderResponse listOrders(UUID userUid) {
        List<CustomerOrderProductItemEntity> orderProductEntityItems = orderProductItemRepository
                .getCustomerOrderProductItemEntitiesByCustomerOrder_UserUid(userUid);
        List<CustomerOrderToppingItemEntity> orderToppingItemEntities = orderToppingItemRepository
                .getOrderToppingItemEntitiesByCustomerOrderProductItemIn(orderProductEntityItems);

        // map product item UID -> list of topping entities for that product item
        Map<UUID, List<CustomerOrderToppingItemEntity>> productItemUUIDToTopping = orderToppingItemEntities
                .stream()
                .collect(
                        groupingBy(o -> o.getCustomerOrderProductItem().getUid())
                );

        // group product items by the parent order UID and map each product item to ExternalOrderProductItemResponse
        Map<UUID, List<ExternalOrderProductItemResponse>> orderUidToProducts = orderProductEntityItems.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCustomerOrder().getUid(),
                        Collectors.mapping(
                                productItem -> new ExternalOrderProductItemResponse(
                                        productItem.getUid(),
                                        productItem.getProduct().getUid(),
                                        productItem.getProduct().getPrice(),
                                        productItem.getQuantity(),
                                        productItem.getCreatedAt(),
                                        // build toppings list for this product item
                                        productItemUUIDToTopping.getOrDefault(productItem.getUid(), List.of())
                                                .stream()
                                                .map(toppingItem -> new ExternalOrderToppingItemResponse(
                                                        toppingItem.getUid(),
                                                        toppingItem.getTopping().getUid(),
                                                        toppingItem.getOriginalPricePerTopping(),
                                                        toppingItem.getQuantity()
                                                ))
                                                .toList()
                                ),
                                Collectors.toList()
                        )
                ));

        // convert grouped entries into ExternalOrderItemResponse instances
        List<ExternalOrderItemResponse> orderItems = orderUidToProducts.entrySet().stream()
                .map(e -> new ExternalOrderItemResponse(e.getKey(), e.getValue()))
                .toList();

        return new ExternalOrderResponse(orderItems);
    }
}
