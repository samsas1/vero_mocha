package com.coffee.steps;

import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;


public class DatabaseSteps {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void cleanDatabaseBeforeScenario() {
        cleanDatabase();
    }

    private void cleanDatabase() {
        jdbcTemplate.execute("DELETE FROM customer_order_topping_item");
        jdbcTemplate.execute("DELETE FROM customer_order_product_item");
        jdbcTemplate.execute("DELETE FROM customer_order");
        jdbcTemplate.execute("DELETE FROM cart_topping_item");
        jdbcTemplate.execute("DELETE FROM cart_product_item");
        jdbcTemplate.execute("DELETE FROM product");
        jdbcTemplate.execute("DELETE FROM topping");
    }
}
