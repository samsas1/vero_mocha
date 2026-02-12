package com.coffee.reporting;

import com.coffee.admin.ProductToppingCountsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@Validated
public class ReportingController {

    private static final Logger log = LoggerFactory.getLogger(ReportingController.class);
    private final ReportingService reportingService;

    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/toppings/most-used")
    public ResponseEntity<ProductToppingCountsResponse> listMostUsedProductToppings() {
        log.info("Received request to list most used product toppings");
        return ResponseEntity.ok(reportingService.generateMostUsedToppingPerProductReport());
    }

}
