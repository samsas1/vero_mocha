package com.coffee.steps;

import com.coffee.admin.ProductRequest;
import com.coffee.admin.ProductResponse;
import com.coffee.admin.ToppingRequest;
import com.coffee.admin.ToppingResponse;
import com.coffee.config.CucumberConfiguration;
import com.coffee.enumerators.ExternalItemStatus;
import io.cucumber.java.Before;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemManagementSteps {

    // Shared context to store created items by name
    public static final Map<String, UUID> productsByName = new HashMap<>();
    public static final Map<String, UUID> toppingsByName = new HashMap<>();
    private static final String TOPPING_BASE_PATH = "/items/toppings";
    private static final String PRODUCT_BASE_PATH = "/items/products";

    @Autowired
    private CucumberConfiguration config;

    private RestClient restClient;

    @Before
    public void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + config.getPort())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @When("An active product named {string} with price {double} is created")
    public void createProduct(String name, double price) {
        ProductRequest productRequest = new ProductRequest(name, BigDecimal.valueOf(price), ExternalItemStatus.ACTIVE);

        ProductResponse createdProduct = restClient.post()
                .uri(PRODUCT_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(productRequest)
                .retrieve()
                .body(ProductResponse.class);

        assertThat(createdProduct).isNotNull();
        String productUid = createdProduct.uid().toString();

        productsByName.put(name, createdProduct.uid());

        ProductResponse fetchedProduct = restClient.get()
                .uri(PRODUCT_BASE_PATH + "/" + productUid)
                .retrieve()
                .body(ProductResponse.class);

        assertThat(fetchedProduct).isNotNull();
        assertThat(fetchedProduct.name()).isEqualTo(name);
        assertThat(fetchedProduct.price()).isEqualByComparingTo(BigDecimal.valueOf(price));
        assertThat(fetchedProduct.itemStatus()).isEqualTo(ExternalItemStatus.ACTIVE);
        assertThat(fetchedProduct.uid()).isNotNull();
        assertThat(fetchedProduct.createdAt()).isNotNull();
        assertThat(fetchedProduct.updatedAt()).isNotNull();
    }

    @When("An active topping named {string} with price {double} is created")
    public void createTopping(String name, double price) {
        ToppingRequest toppingRequest = new ToppingRequest(name, BigDecimal.valueOf(price), ExternalItemStatus.ACTIVE);

        ToppingResponse createdTopping = restClient.post()
                .uri(TOPPING_BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(toppingRequest)
                .retrieve()
                .body(ToppingResponse.class);

        assertThat(createdTopping).isNotNull();
        String toppingUid = createdTopping.uid().toString();

        toppingsByName.put(name, createdTopping.uid());

        ToppingResponse fetchedTopping = restClient.get()
                .uri(TOPPING_BASE_PATH + "/" + toppingUid)
                .retrieve()
                .body(ToppingResponse.class);

        assertThat(fetchedTopping).isNotNull();
        assertThat(fetchedTopping.name()).isEqualTo(name);
        assertThat(fetchedTopping.price()).isEqualByComparingTo(BigDecimal.valueOf(price));
        assertThat(fetchedTopping.itemStatus()).isEqualTo(ExternalItemStatus.ACTIVE);
        assertThat(fetchedTopping.uid()).isNotNull();
        assertThat(fetchedTopping.createdAt()).isNotNull();
        assertThat(fetchedTopping.updatedAt()).isNotNull();
    }
}
