package com.coffee;

import com.coffee.admin.MostUsedProductToppingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/reports")
@Validated
public class ReportingController {

    private static final Logger log = LoggerFactory.getLogger(ReportingController.class);

    @Autowired
    private final RestClient coreClient;

    public ReportingController(RestClient coreClient) {
        this.coreClient = coreClient;
    }

    @GetMapping("/toppings/most-used")
    public ResponseEntity<MostUsedProductToppingResponse> listMostUsedToppings() {
        log.info("Received request to list most used product toppings");
        return coreClient.get()
                .uri("/reports/toppings/most-used")
                .retrieve()
                .toEntity(MostUsedProductToppingResponse.class);
    }
}
