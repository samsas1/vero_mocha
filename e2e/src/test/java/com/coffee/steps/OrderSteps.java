package com.coffee.steps;

import com.coffee.config.CucumberConfiguration;
import com.coffee.publicapi.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

import static com.coffee.steps.ItemManagementSteps.productsByName;
import static com.coffee.steps.ItemManagementSteps.toppingsByName;
import static org.assertj.core.api.Assertions.assertThat;

public class OrderSteps {

    private static final String ORDER_BASE_PATH = "/orders";

    @Autowired
    private CucumberConfiguration config;
    private RestClient restClient;
    private UUID currentUserUid;

    @Before
    public void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:5002")
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();

        currentUserUid = UUID.fromString("7ad5bc4e-0de9-41dc-a5b6-745c1debba23");
    }

    @When("The user places an order")
    public void placeOrder() {
        ExternalOrderPlacementResponse orderResponse = restClient.post()
                .uri(ORDER_BASE_PATH)
                .header("user", currentUserUid.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(ExternalOrderPlacementResponse.class);

        assertThat(orderResponse).isNotNull();
        assertThat(orderResponse.orderUid()).isNotNull();
        assertThat(orderResponse.originalPrice()).isNotNull();
        assertThat(orderResponse.finalPrice()).isNotNull();
    }

    @Then("The order has original price {double} and final price {double}")
    public void orderHasPrices(double originalPrice, double finalPrice) {
        ExternalOrderResponse orderListResponse = restClient.get()
                .uri(ORDER_BASE_PATH)
                .header("user", currentUserUid.toString())
                .retrieve()
                .body(ExternalOrderResponse.class);

        assertThat(orderListResponse).isNotNull();
        assertThat(orderListResponse.orders()).isNotEmpty();

        ExternalOrderItemResponse order = orderListResponse.orders().get(0);

        assertThat(order.originalPrice()).isEqualByComparingTo(BigDecimal.valueOf(originalPrice));
        assertThat(order.finalPrice()).isEqualByComparingTo(BigDecimal.valueOf(finalPrice));
    }

    @Then("The order has product named {string}, quantity {int}, and price {double} with topping named {string}, quantity {int}, and price {double}")
    public void orderProductHasToppingWithQuantityAndPrice(String productName,
                                                           int productQuantity,
                                                           double productPrice,
                                                           String toppingName,
                                                           int toppingQuantity,
                                                           double toppingPrice) {
        ExternalOrderResponse orderListResponse = getOrderListResponse();

        ExternalOrderItemResponse order = orderListResponse.orders().getFirst();
        UUID productUid = productsByName.get(productName);
        UUID toppingUid = toppingsByName.get(toppingName);
        ExternalOrderProductItemResponse productItem = order.items().getFirst();
        ExternalOrderToppingItemResponse toppingItem = productItem.toppings().getFirst();

        assertThat(productItem.productUid()).isEqualTo(productUid);
        assertThat(productItem.quantity()).isEqualTo(productQuantity);
        assertThat(productItem.price()).isEqualByComparingTo(BigDecimal.valueOf(productPrice));
        assertThat(toppingItem.toppingUid()).isEqualTo(toppingUid);
        assertThat(toppingItem.quantity()).isEqualTo(toppingQuantity);
        assertThat(toppingItem.price()).isEqualByComparingTo(BigDecimal.valueOf(toppingPrice));
    }


    private ExternalOrderResponse getOrderListResponse() {
        ExternalOrderResponse orderListResponse = restClient.get()
                .uri(ORDER_BASE_PATH)
                .header("user", currentUserUid.toString())
                .retrieve()
                .body(ExternalOrderResponse.class);

        assertThat(orderListResponse).isNotNull();
        assertThat(orderListResponse.orders()).isNotEmpty();

        return orderListResponse;
    }
}
