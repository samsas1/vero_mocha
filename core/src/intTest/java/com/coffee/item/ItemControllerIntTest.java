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
public class ItemControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ToppingRequest toppingDTO;
    private ProductRequest productDTO;

    @BeforeEach
    public void setup() {
        toppingDTO = Instancio.of(ToppingRequest.class)
                .set(field("price"), 2.55)
                .create();
        productDTO = Instancio.of(ProductRequest.class)
                .set(field("price"), 2.55)
                .create();
    }

    @Test
    public void whenToppingInserted_thenItCanBeFetched() throws Exception {
        toppingDTO = Instancio.create(ToppingRequest.class);
        String request = objectMapper.writeValueAsString(toppingDTO);

        String response = mockMvc.perform(post("/item/topping").content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID uuid = objectMapper.readValue(response, UUID.class);

        mockMvc.perform(get("/item/topping/" + uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(toppingDTO.price()))
                .andExpect(jsonPath("$.name").value(toppingDTO.name()))
                .andExpect(jsonPath("$.itemStatus").value("ACTIVE"));

    }

    @Test
    public void whenProductInserted_thenItCanBeFetched() throws Exception {
        productDTO = Instancio.create(ProductRequest.class);
        String request = objectMapper.writeValueAsString(productDTO);

        String response = mockMvc.perform(post("/item/product").content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID uuid = objectMapper.readValue(response, UUID.class);

        mockMvc.perform(get("/item/product/" + uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(productDTO.price()))
                .andExpect(jsonPath("$.name").value(productDTO.name()))
                .andExpect(jsonPath("$.itemStatus").value("ACTIVE"));

    }

    @Test
    public void whenProductsListed_thenTheyAreReturnedByMostRecent() {
        // TODO
    }

    @Test
    public void whenToppingsListed_thenTheyAreReturnedByMostRecent() {
        // TODO
    }

}
