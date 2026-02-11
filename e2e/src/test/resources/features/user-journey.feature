Feature: Order creation journey


  Scenario: Add product with topping to cart and clear
    When An active product named "Espresso" with price 4.0 is created
    And An active topping named "Chocolate" with price 1.5 is created
    And The user cart is empty

    When The user adds a product named "Espresso" with quantity 1 and topping "Chocolate" with quantity 2 to the cart
    Then The cart has product named "Espresso", quantity 1, and price 4.0 with topping named "Chocolate", quantity 2, and price 1.5

    When The user clears the cart
    Then The user cart is empty

  Scenario: Add product with topping to cart and order
    When An active product named "Espresso" with price 4.0 is created
    And An active topping named "Chocolate" with price 1.5 is created
    And The user cart is empty

    When The user adds a product named "Espresso" with quantity 1 and topping "Chocolate" with quantity 2 to the cart
    Then The cart has product named "Espresso", quantity 1, and price 4.0 with topping named "Chocolate", quantity 2, and price 1.5

    When The user places an order
    Then The order has product named "Espresso", quantity 1, and price 4.0 with topping named "Chocolate", quantity 2, and price 1.5
    And The order has original price 7 and final price 7
    And The user cart is empty




