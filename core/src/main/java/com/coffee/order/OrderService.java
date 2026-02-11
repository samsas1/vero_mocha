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

import java.math.BigDecimal;
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

        // Wipe cart as items are now ordered
        cartItemService.clearCart(userUid);
        return new ExternalOrderPlacementResponse(
                orderUid,
                discountResponse.originalPrice(),
                discountResponse.finalPrice()
        );
    }

    public ExternalOrderResponse listOrders(UUID userUid) {
        // Fetch all product order items for user
        List<CustomerOrderProductItemEntity> orderProductEntityItems = orderProductItemRepository
                .getCustomerOrderProductItemEntitiesByCustomerOrder_UserUid(userUid);
        // Fetch all topping order items for the product order items
        List<CustomerOrderToppingItemEntity> orderToppingItemEntities = orderToppingItemRepository
                .getOrderToppingItemEntitiesByCustomerOrderProductItemIn(orderProductEntityItems);

        // Establish a product item -> topping item relationship
        // This is done by mappin product item UUIDs to topping entities.
        Map<UUID, List<CustomerOrderToppingItemEntity>> productItemUUIDToTopping = orderToppingItemEntities
                .stream()
                .collect(groupingBy(CustomerOrderToppingItemEntity::getProductItemUid));

        // Establish an order key -> product item relationship.
        // This is done by mapping orders to products
        // In order to have access to order prices, an order key is created which contains the order uid and prices.
        // The product items are mapped to external response objects which are ready to be placed in the final response
        Map<OrderWithPrices, List<ExternalOrderProductItemResponse>> orderKeyToProductItems = orderProductEntityItems.stream()
                .collect(Collectors.groupingBy(
                        this::mapOrderKey,
                        Collectors.mapping(
                                productItem -> {
                                    List<ExternalOrderToppingItemResponse> toppings = productItemUUIDToTopping.getOrDefault(productItem.getUid(), List.of())
                                            .stream()
                                            .map(this::mapExternalToppingResponse)
                                            .toList();
                                    return mapExternalProductResponse(productItem, toppings);
                                },
                                Collectors.toList()
                        )
                ));

        // Collect into external response by iterating over the order key -> product item map and adding each order
        // as a response entity
        List<ExternalOrderItemResponse> orderItems = orderKeyToProductItems.entrySet().stream()
                .map(e -> {
                            OrderWithPrices orderWithPrices = e.getKey();
                            List<ExternalOrderProductItemResponse> products = e.getValue();
                            return new ExternalOrderItemResponse(
                                    orderWithPrices.orderUid(),
                                    products,
                                    orderWithPrices.originalPrice(),
                                    orderWithPrices.finalPrice
                            );
                        }
                )
                .toList();
        return new ExternalOrderResponse(orderItems);
    }

    private OrderWithPrices mapOrderKey(CustomerOrderProductItemEntity entity) {
        CustomerOrderEntity order = entity.getCustomerOrder();
        return new OrderWithPrices(
                order.getUid(),
                order.getOriginalPrice(),
                order.getFinalPrice()
        );
    }

    private ExternalOrderToppingItemResponse mapExternalToppingResponse(CustomerOrderToppingItemEntity entity) {
        return new ExternalOrderToppingItemResponse(
                entity.getUid(),
                entity.getTopping().getUid(),
                entity.getOriginalPricePerTopping(),
                entity.getQuantity()
        );
    }

    private ExternalOrderProductItemResponse mapExternalProductResponse(CustomerOrderProductItemEntity entity,
                                                                        List<ExternalOrderToppingItemResponse> toppings) {
        return new ExternalOrderProductItemResponse(
                entity.getUid(),
                entity.getProduct().getUid(),
                entity.getOriginalPricePerProduct(),
                entity.getQuantity(),
                entity.getCreatedAt(),
                toppings
        );
    }

    private record OrderWithPrices(UUID orderUid, BigDecimal originalPrice, BigDecimal finalPrice) {
    }
}
