package com.coffee.item;

import com.coffee.admin.ProductRequest;
import com.coffee.admin.ToppingRequest;
import jakarta.transaction.Transactional;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.instancio.Select.field;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ItemManagementControllerIntTest {

    private static final String TOPPING_ENDPOINT = "/item/toppings";
    private static final String PRODUCT_ENDPOINT = "/item/products";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private ToppingRequest toppingDTO;
    private ProductRequest productDTO;

    @BeforeEach
    void setup() {
        toppingDTO = Instancio.of(ToppingRequest.class)
                .set(field("price"), 2.55)
                .create();
        productDTO = Instancio.of(ProductRequest.class)
                .set(field("price"), 2.55)
                .create();
    }

    @Test
    void whenToppingInserted_thenItCanBeFetched() throws Exception {
        toppingDTO = Instancio.create(ToppingRequest.class);
        String request = objectMapper.writeValueAsString(toppingDTO);

        // Insert the topping and get the UUID
        String response = mockMvc.perform(post(TOPPING_ENDPOINT).content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID uuid = objectMapper.readValue(response, UUID.class);

        // Fetch the topping using the UUID and verify the response
        mockMvc.perform(get(TOPPING_ENDPOINT + "/" + uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(toppingDTO.price()))
                .andExpect(jsonPath("$.name").value(toppingDTO.name()))
                .andExpect(jsonPath("$.itemStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.uid").value(uuid.toString()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void whenProductInserted_thenItCanBeFetched() throws Exception {
        productDTO = Instancio.create(ProductRequest.class);
        String request = objectMapper.writeValueAsString(productDTO);

        // Insert the product and get the UUID
        String response = mockMvc.perform(post(PRODUCT_ENDPOINT).content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID uuid = objectMapper.readValue(response, UUID.class);

        // Fetch the product using the UUID and verify the response
        mockMvc.perform(get(PRODUCT_ENDPOINT + "/" + uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(productDTO.price()))
                .andExpect(jsonPath("$.name").value(productDTO.name()))
                .andExpect(jsonPath("$.itemStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.uid").value(uuid.toString()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void whenProductFetchedWithInvalidUid_thenNotFoundReturned() throws Exception {
        mockMvc.perform(get(PRODUCT_ENDPOINT + "/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenToppingFetchedWithInvalidUid_thenNotFoundReturned() throws Exception {
        mockMvc.perform(get(TOPPING_ENDPOINT + "/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenProductsListed_thenTheyAreReturnedByMostRecent() {
        // TODO
    }

    @Test
    void whenToppingsListed_thenTheyAreReturnedByMostRecent() {
        // TODO
    }

    @Test
    void whenProductUpdated_thenUpdatedItemIsReturned() {
        // TODO
    }

    @Test
    void whenProductUpdatedWithNoStatusSpecified_thenStatusRemainsUnchanged() {
        // TODO
    }

    @Test
    void whenToppingUpdatedWithNoStatusSpecified_thenStatusRemainsUnchanged() {
        // TODO
    }

    @Test
    void whenToppingUpdated_thenUpdatedItemIsReturned() {
        // TODO
    }

    @Test
    void whenToppingUpdatedWithInvalidUid_thenNotFoundReturned() {
        // TODO
    }

    @Test
    void whenProductUpdatedWithInvalidUid_thenNotFoundReturned() {
        // TODO
    }

    @Test
    void whenProductIsDeleted_thenFetchingReturnsNotFound() {
        // TODO
    }

    @Test
    void whenToppingIsDeleted_thenFetchingReturnsNotFound() {
        // TODO
    }

    @Test
    void whenToppingLinkedToCartAndOrder_thenDeleteThrowsIllegalStateException() {
        // TODO
    }

    @Test
    void whenProductLinkedToCartAndOrder_thenDeleteThrowsIllegalStateException() {
        // TODO
    }


}
