package com.coffee.reporting;

import com.coffee.admin.MostUsedProductToppingResponse;
import com.coffee.admin.MostUsedToppingProductResponse;
import com.coffee.reporting.custom.query.ReportingRepository;
import com.coffee.reporting.entity.MostUsedToppingPerDrink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReportingService {

    @Autowired
    private final ReportingRepository reportingRepository;

    public ReportingService(ReportingRepository reportingRepository) {
        this.reportingRepository = reportingRepository;
    }

    public MostUsedProductToppingResponse generateMostUsedToppingPerDrinkReport() {
        List<MostUsedToppingProductResponse> mostUsedToppingProductResponseList = reportingRepository
                .listMostUsedToppingsForDrinks()
                .stream().map(this::map).toList();
        return new MostUsedProductToppingResponse(mostUsedToppingProductResponseList);
    }

    private MostUsedToppingProductResponse map(MostUsedToppingPerDrink mostUsed) {
        return new MostUsedToppingProductResponse(
                mostUsed.productUid(),
                mostUsed.productName(),
                mostUsed.toppingUid(),
                mostUsed.toppingName(),
                mostUsed.totalToppingQuantity()
        );
    }

}
