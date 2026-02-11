package com.coffee.steps;

import com.coffee.config.CucumberConfiguration;
import com.coffee.publicapi.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.coffee.steps.ItemManagementSteps.productsByName;
import static com.coffee.steps.ItemManagementSteps.toppingsByName;
import static org.assertj.core.api.Assertions.assertThat;

public class CartSteps {

    private static final String CART_BASE_PATH = "/cart/items";

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

    @Given("The user cart is empty")
    public void checkCartEmpty() {
        ExternalCartItemResponse cartResponse = getCartResponse();
        assertThat(cartResponse).isNotNull();
        assertThat(cartResponse.items()).isEmpty();
    }


    @When("The user adds a product named {string} with quantity {int} and topping {string} with quantity {int} to the cart")
    public void addProductWithToppingToCart(String productName, int productQuantity, String toppingName, int toppingQuantity) {
        UUID productUid = productsByName.get(productName);
        UUID toppingUid = toppingsByName.get(toppingName);

        assertThat(productUid)
                .isNotNull();
        assertThat(toppingUid)
                .isNotNull();

        ExternalToppingItemRequest toppingItem = new ExternalToppingItemRequest(
                toppingUid,
                toppingQuantity
        );

        ExternalCartItemRequest cartItemRequest = new ExternalCartItemRequest(
                productUid,
                productQuantity,
                List.of(toppingItem)
        );

        UUID cartItemUid = restClient.post()
                .uri(CART_BASE_PATH)
                .header("user", currentUserUid.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .body(cartItemRequest)
                .retrieve()
                .body(UUID.class);

        assertThat(cartItemUid).isNotNull();
    }

    @Then("The cart has product named {string}, quantity {int}, and price {double} with topping named {string}, quantity {int}, and price {double}")
    public void theCartItemShouldHaveProductWithQuantityAndPrice(String productName,
                                                                 int productQuantity,
                                                                 double productPrice,
                                                                 String toppingName,
                                                                 int toppingQuantity,
                                                                 double toppingPrice) {
        ExternalCartItemResponse cartResponse = getCartResponse();

        assertThat(cartResponse).isNotNull();
        assertThat(cartResponse.items()).hasSize(1);

        UUID productUid = productsByName.get(productName);
        UUID toppingUid = toppingsByName.get(toppingName);

        ExternalCartProductItemResponse productItem = cartResponse.items().getFirst();

        assertThat(productItem.productUid()).isEqualTo(productUid);
        assertThat(productItem.quantity()).isEqualTo(productQuantity);
        assertThat(productItem.price()).isEqualByComparingTo(BigDecimal.valueOf(productPrice));

        ExternalCartToppingItemResponse toppingItem = productItem.toppings().getFirst();

        assertThat(toppingItem.toppingUid()).isEqualTo(toppingUid);
        assertThat(toppingItem.quantity()).isEqualTo(toppingQuantity);
        assertThat(toppingItem.price()).isEqualByComparingTo(BigDecimal.valueOf(toppingPrice));

    }

    @When("The user clears the cart")
    public void theUserClearsTheCart() {
        ResponseEntity<Void> response = restClient.delete()
                .uri(CART_BASE_PATH)
                .header("user", currentUserUid.toString())
                .retrieve()
                .toBodilessEntity();
    }

    private ExternalCartItemResponse getCartResponse() {
        return restClient.get()
                .uri(CART_BASE_PATH)
                .header("user", currentUserUid.toString())
                .retrieve()
                .body(ExternalCartItemResponse.class);
    }
}
