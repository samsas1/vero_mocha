# Vero Mocha - Online Coffee Shop

A microservices-based online coffee shop built with Spring Boot. Comes with basic functionality required by any online
coffee shop.

## Concepts

- Customer - a public user of the online coffee shop
- Admin - a privileged user who can manage offerings (products, items) and view reports
- Product - an item available for purchase (e.g. espresso, cappuccino, etc.)
- Topping - an optional add-on for products (e.g. extra shot, whipped cream, etc.)
- Cart - a collection of products and toppings that a customer intends to purchase
- Cart product item - an instance of a product in a customer's cart, has a quantity
- Cart topping item - an instance of a topping added to a product in a customer's cart, has a quantity (per product
  item, such that total flattened quantity of a topping in a cart is product item quantity x topping item quantity)
- Order - a finalized purchase containing product items and toppings, with applied discounts
- Order product item - a product item purchased by a customer, structurally similar to cart product item
- Order topping item - a topping item purchased by a customer for a product item, structurally similar to cart topping
  item

## Features

Customers:

- Browse products and toppings
- Manage their shopping cart
- Place orders with automatic discount application
- View their order history

Admins:

- Manage products and toppings
- View business analytics

## Table of Contents

- [Structure](#structure)
- [Tech Stack](#tech-stack)
- [Assumptions](#assumptions)
- [Getting Started](#getting-started)
- [User Journeys](#user-journeys)
- [API Documentation](#api-documentation)
    - [Public API](#public-api)
        - [Menu Browsing API](#menu-browsing-api)
        - [Cart API](#cart-api)
        - [Order API](#order-api)
    - [Admin API](#admin-api)
        - [Item Management API](#item-management-api)
        - [Reporting API](#reporting-api)
- [Testing Implementation](#testing-implementation)
- [TODO](#todo)

---

## Structure

The project follows a **package-by-feature multi-module microservices structure**:

```
vero_mocha/
├── admin-api/         # Admin-facing API
├── core/              # Main business logic and data persistence
├── commons/           # Shared DTOs and utilities
├── e2e/               # End-to-end tests
├── public-api/        # Public-facing API
```

### Module Responsibilities

- **admin-api**: Request forwarding service for administrative operations.
- **core**: Contains all business services, repositories, and REST controllers. Uses PostgreSQL for persistence.
- **commons**: Shared data transfer objects (DTOs) and common utilities used across all modules.
- **e2e**: Integration and end-to-end tests.
- **public-api**: Request forwarding service for public operations.

API's are separated from core for network isolation purposes. Ideally, the Admin-api would not be accessible without a
VPN connection to the company VPC. Only Public-api should accept requests from the public internet.

## Assumptions

- The same toppings can be added to the same product item multiple times (i.e. 2 extra shots on the same espresso)
- All toppings available for all items (we will allow lemons on cappuccinos)
- User management is excluded
- One cart per user (no guest users)

## Tech Stack

- **Framework**: Spring Boot 4.0.2
- **Language**: Java 25
- **Build Tool**: Gradle 9.2.x
- **Database**: PostgreSQL 16
- **ORM**: Spring Data JPA with Hibernate
- **Database Migrations**: Flyway
- **JSON Processing**: Jackson
- **Testing**: JUnit 5, Spring Boot Test, Cucumber

## Getting Started

### Prerequisites

- Docker

### Setup and Run Locally

#### Using Docker Compose (Recommended)

```bash
# Clone the repository
git clone https://github.com/samsas1/vero_mocha.git
cd vero_mocha

# Start all services 
docker-compose up --build

```

### Running Tests locally (requires gradlew)

```bash
# Run all unit tests
./gradlew :core:test

# Run integration tests (core module)
./gradlew :core:intTest

# Run end-to-end tests
# Hits the database and services so make sure you run them via docker as above or manually
./gradlew :e2e:test
```

### Postman

Postman collection to aid local manual tests:
https://www.postman.com/adomas-1333/workspace/public/collection/16262468-c21b9ded-7433-4bab-92de-67a63a099b18?action=share&creator=16262468

Admin endpoints will work out of the box.
For public endpoints, add a USER environment variable with the value `7ad5bc4e-0de9-41dc-a5b6-745c1debba23` to
authenticate as the hardcoded user.

## User Journeys

### Admin Management

**Actor**: Coffee shop administrator

1. **Manage Products**
    - Admin creates new products (name, price, status)
    - Admin can update product details (name, price, status)
    - Admin can view all products (including inactive ones)
    - Admin can delete products (if they have no orders or are not in any cart)

2. **Manage Toppings**
    - Admin creates new toppings (name, price, status)
    - Admin can update topping details (name, price, status)
    - Admin can view all toppings
    - Admin can delete toppings (if they have no orders or cart items)

3. **View Business Metrics**
    - Admin generates reports on most-used toppings per product

### Customer

**Actor**: Coffee shop administrator

1. **Browse Menu**
    - Customer requests the list of products
    - API returns all products currently available for purchase
    - Customer requests the list of toppings
    - API returns all toppings currently available for purchase

2. **Build Cart**
    - Customer adds a product (with optional toppings) with quantities to cart
    - Customer requests cart items to review
    - (Optional) Customer clears cart if they want to start over

3. **Finalize order**
    - Customer fetches cart items to review
    - Customer requests applicable discount information for current cart

4. **Place Order**
    - Customer places an order from their cart
    - System creates an order record with current cart items and applied discount
    - Cart is automatically cleared
    - Order confirmation is returned with order ID

5. **View Order History**
    - Customer can view all their previous orders
    - Each order shows products, toppings, prices, and order status

-

## API Documentation

### Common Response Format

Success responses follow this format:

```json
{
  "data": {
    /* response data */
  },
  "timestamp": "2026-02-13T10:30:00Z",
  "status": 200
}
```

Error responses:

```json
{
  "error": "Error message",
  "timestamp": "2026-02-13T10:30:00Z",
  "status": 400
}
```

---

## Public API

Public APIs are customer-facing endpoints for browsing the menu, managing shopping carts, and placing orders.

### Authentication

All customer-facing endpoints require the `user` header. In a real application, this would come from a JWT token,
linked to a user account in the database. For the toy application purposes, the authentication flow and user table is
skipped. There is one hardcoded user available for testing:

```
user: 7ad5bc4e-0de9-41dc-a5b6-745c1debba23
```

### Menu Browsing API

Public endpoints for browsing available products and toppings.

### 1. List Active Products

Browse all products currently available for purchase.

**Endpoint**: `GET /menu/products`

**Headers**: `user`

**Response**:

```json
{
  "products": [
    {
      "uid": "824345c4-6ef5-410d-87d3-2ec7d4a5278b",
      "name": "Espresso",
      "price": "3.50",
      "itemStatus": "ACTIVE"
    },
    {
      "uid": "550e8400-e29b-41d4-a716-446655440001",
      "name": "Cappuccino",
      "price": "4.50",
      "itemStatus": "ACTIVE"
    }
  ]
}
```

---

### 2. List Active Toppings

Browse all toppings available to add to products.

**Endpoint**: `GET /menu/toppings`

**Headers**: `user`

**Response**:

```json
{
  "toppings": [
    {
      "uid": "c05d573d-9bc1-4915-be90-6fe6416bef99",
      "name": "Extra Shot",
      "price": "0.75",
      "itemStatus": "ACTIVE"
    },
    {
      "uid": "660e8400-e29b-41d4-a716-446655440001",
      "name": "Whipped Cream",
      "price": "0.50",
      "itemStatus": "ACTIVE"
    }
  ]
}
```

---

## Cart API

### 1. Add Item to Cart

Add a product (with optional toppings) to the user's cart.

**Endpoint**: `POST /cart/items`

**Headers**: `user`

**Request Body**:

```json
{
  "productUid": "824345c4-6ef5-410d-87d3-2ec7d4a5278b",
  "quantity": 2,
  "toppings": [
    {
      "toppingUid": "c05d573d-9bc1-4915-be90-6fe6416bef99",
      "quantity": 1
    },
    {
      "toppingUid": "660e8400-e29b-41d4-a716-446655440001",
      "quantity": 2
    }
  ]
}
```

**Response**:

Currently, a UUID representing the item placed into cart. To be updated to return a full item information (including
topping and product details).
including topping and product details).

**Notes**:

- Toppings are optional; omit the `toppings` array if no toppings are needed
- Quantity must be greater than 0 (for both product and topping if adding any)
- The same topping cannot be added multiple times to the same item (enforced during order placement)
- The product quantity controls the count of the product itself, while the topping quantity controls how many times that
  topping is added to the product item
- The total number of toppings in the cart item is the multiplication of topping quantity and product quantity

E.g.
If you want 1 cappuccino with 2 extra shots, you would send:

```json
{
  "productUid": "CAPPUCCINO_UUID",
  "quantity": 1,
  "toppings": [
    {
      "toppingUid": "EXTRA_SHOT_UUID",
      "quantity": 2
    }
  ]
}
```

If you want 2 cappuccinos with 2 extra shots each, you would send:

```json
{
  "productUid": "CAPPUCCINO_UUID",
  "quantity": 2,
  "toppings": [
    {
      "toppingUid": "EXTRA_SHOT_UUID",
      "quantity": 2
    }
  ]
}
```

If you want 1 cappuccino with 1 extra shot and 1 cappuccino with 2 extra shots, you would need to two separate requests:

```json
{
  "productUid": "CAPPUCCINO_UUID",
  "quantity": 1,
  "toppings": [
    {
      "toppingUid": "EXTRA_SHOT_UUID",
      "quantity": 1
    }
  ]
}
```

```json
{
  "productUid": "CAPPUCCINO_UUID",
  "quantity": 1,
  "toppings": [
    {
      "toppingUid": "EXTRA_SHOT_UUID",
      "quantity": 2
    }
  ]
}
```

Adding the same topping twice for a product item through repetition instead of quantity is not supported, so the
following
will fail:

```json
{
  "productUid": "CAPPUCCINO_UUID",
  "quantity": 1,
  "toppings": [
    {
      "toppingUid": "EXTRA_SHOT_UUID",
      "quantity": 1
    },
    {
      "toppingUid": "EXTRA_SHOT_UUID",
      "quantity": 1
    }
  ]
}
```

---

### 2. Get Cart Items

Retrieve all items currently in the user's cart with pricing.

**Endpoint**: `GET /cart/items`

**Headers**: `user`

**Response**:

```json
{
  "totalPrice": "15.50",
  "cartItems": [
    {
      "cartProductItemUid": "550e8400-e29b-41d4-a716-446655440005",
      "productUid": "550e8400-e29b-41d4-a716-446655440001",
      "productName": "Cappuccino",
      "price": "4.50",
      "quantity": 2,
      "toppings": [
        {
          "cartToppingItemUid": "770e8400-e29b-41d4-a716-446655440000",
          "toppingUid": "c05d573d-9bc1-4915-be90-6fe6416bef99",
          "toppingName": "Extra Shot",
          "price": "0.75",
          "quantity": 1
        },
        {
          "cartToppingItemUid": "770e8400-e29b-41d4-a716-446655440001",
          "toppingUid": "660e8400-e29b-41d4-a716-446655440001",
          "toppingName": "Whipped Cream",
          "price": "0.50",
          "quantity": 2
        }
      ]
    }
  ]
}
```

---

### 3. Get Cart Discount

Retrieve the applicable discount for the user's current cart.

**Endpoint**: `GET /cart/discount`

**Headers**: `user`

**Response**:

```json
{
  "discountType": "NO_DISCOUNT",
  "originalPrice": "5.50",
  "finalPrice": "5.50"
}
```

Or with discount:

```json
{
  "discountType": "LARGE_ORDER_DISCOUNT",
  "originalPrice": "25.00",
  "finalPrice": "20.00"
}
```

**Discount Types**:

- `NO_DISCOUNT`: No discount applicable
- `FULL_CART_DISCOUNT`: Percentage discount on entire cart
- `LARGE_ORDER_DISCOUNT`: Free item for large orders

---

### 4. Clear Cart

Remove all items from the user's cart.

**Endpoint**: `DELETE /cart/items`

**Headers**: `user`

**Response**:
Empty body with 200 OK
---

## Order API

Place and retrieve customer orders. All endpoints require the `user` header.

### 1. Place Order

Convert the current cart into an order with applicable discounts.

**Endpoint**: `POST /orders`

**Headers**: `user`

**Request Body**: Empty

**Response**:

```json
{
  "orderUid": "1cfa90cf-f4b7-4579-bc2f-ead97b5c0e64",
  "originalPrice": 12.50,
  "finalPrice": 9.50,
  "placed": true,
  "message": "Order placed successfully!"
}
```

Or on empty cart

```json
{
  "orderUid": null,
  "originalPrice": null,
  "finalPrice": null,
  "placed": false,
  "message": "No order created. Cart is empty."
}
```

---

### 2. List Orders

Retrieve all orders placed by the user, including order details, items, toppings, and pricing.

**Endpoint**: `GET /orders`

**Headers**: `user`

**Request Body**: Empty

**Response**:

```json
{
  "orders": [
    {
      "orderUid": "8eb76e33-4548-43b8-b7e1-c1e063d04349",
      "items": [
        {
          "orderProductItemUid": "27b98fce-5e3a-4aa5-a17f-49dd252917a7",
          "productUid": "8fd44970-da95-4a7f-b89c-29d480307cd5",
          "productName": "Espresso",
          "price": 4.0,
          "quantity": 1,
          "createdAt": "2026-02-11T22:45:23.521330Z",
          "toppings": [
            {
              "orderToppingItemUid": "aacc2d0f-219c-4892-959b-b09459673e71",
              "toppingUid": "9206935b-3ecf-4d44-84a9-95aef0fad689",
              "toppingName": "Chocolate",
              "price": 1.5,
              "quantity": 2
            }
          ]
        }
      ],
      "originalPrice": 7.0,
      "finalPrice": 7.0
    }
  ]
}
```

---

## Admin API

Administrative endpoints for managing products, toppings, and generating business reports.

## Authentication

No authentication method is implemented for the Admin API. In a prod applications, all users would send a JWT token that
would
be used to validate the authorization to perform actions. These actions would be stored in history tables with links to
users.

### Item Management API

Manage products and toppings in the catalog.

#### Topping Management

##### 1. Create Topping

Create a new topping in the catalog.

**Endpoint**: `POST /items/toppings`

**Request Body**:

```json
{
  "name": "Extra Shot",
  "price": "0.75",
  "itemStatus": "ACTIVE"
}
```

**Notes**:

- The itemStatus is optional, can be set to ACTIVE or INACTIVE
- Two toppings with the same name are not allowed

**Response**:

```json
{
  "uid": "c05d573d-9bc1-4915-be90-6fe6416bef99",
  "name": "Extra Shot",
  "price": "0.75",
  "itemStatus": "ACTIVE",
  "createdAt": "2026-02-13T10:30:00Z",
  "updatedAt": "2026-02-13T10:30:00Z"
}
```

---

##### 2. List All Toppings

Retrieve all toppings, including inactive ones.

**Endpoint**: `GET /items/toppings`

**Response**:

```json
{
  "toppings": [
    {
      "uid": "c05d573d-9bc1-4915-be90-6fe6416bef99",
      "name": "Extra Shot",
      "price": "0.75",
      "itemStatus": "ACTIVE",
      "createdAt": "2026-02-13T10:30:00Z",
      "updatedAt": "2026-02-13T10:30:00Z"
    }
  ]
}
```

**Notes**:

- Unlike the public browsing endpoint, this includes INACTIVE toppings

---

##### 3. Get Topping by ID

Retrieve a specific topping by its unique identifier.

**Endpoint**: `GET /items/toppings/{uid}`

**Path Parameters**:

- `uid`: UUID - Topping identifier

**Response**:

```json
{
  "uid": "c05d573d-9bc1-4915-be90-6fe6416bef99",
  "name": "Extra Shot",
  "price": "0.75",
  "itemStatus": "ACTIVE",
  "createdAt": "2026-02-13T10:30:00Z",
  "updatedAt": "2026-02-13T10:30:00Z"
}
```

---

##### 4. Update Topping

Update an existing topping's details. The itemStatus can be switched between ACTIVE and INACTIVE to control availability
without deleting the record.

**Endpoint**: `PUT /items/toppings/{uid}`

**Path Parameters**:

- `uid`: UUID - Topping identifier

**Request Body**:

```json
{
  "name": "Extra Shot",
  "price": "0.85",
  "itemStatus": "ACTIVE"
}
```

**Response**:

```json
{
  "uid": "c05d573d-9bc1-4915-be90-6fe6416bef99",
  "name": "Extra Shot",
  "price": "0.85",
  "itemStatus": "ACTIVE",
  "createdAt": "2026-02-13T10:30:00Z",
  "updatedAt": "2026-02-13T14:20:00Z"
}
```

**Notes**:

- Use this endpoint to mark toppings as INACTIVE instead of deleting them if they are linked to orders or carts
- Two toppings with the same name are not allowed

---

##### 5. Delete Topping

Delete a topping from the catalog.

**Endpoint**: `DELETE /items/toppings/{uid}`

**Path Parameters**:

- `uid`: UUID - Topping identifier

**Response**: Empty body with 200 OK

**Notes**:

- This endpoint will fail if the topping has associated orders or is in any cart
- In such cases, use the update endpoint to mark the topping as INACTIVE instead

---

#### Product Management

##### 1. Create Product

Create a new product in the catalog.

**Endpoint**: `POST /items/products`

**Request Body**:

```json
{
  "name": "Latte",
  "price": "5.00",
  "itemStatus": "ACTIVE"
}
```

**Notes**:

- The itemStatus is optional, can be set to ACTIVE or INACTIVE
- Two products with the same name are not allowed

**Response**:

```json
{
  "uid": "824345c4-6ef5-410d-87d3-2ec7d4a5278b",
  "name": "Latte",
  "price": "5.00",
  "itemStatus": "ACTIVE",
  "createdAt": "2026-02-13T10:30:00Z",
  "updatedAt": "2026-02-13T10:30:00Z"
}
```

---

##### 2. List All Products

Retrieve all products, including inactive ones.

**Endpoint**: `GET /items/products`

**Response**:

```json
{
  "products": [
    {
      "uid": "824345c4-6ef5-410d-87d3-2ec7d4a5278b",
      "name": "Espresso",
      "price": "3.50",
      "itemStatus": "ACTIVE",
      "createdAt": "2026-02-13T10:30:00Z",
      "updatedAt": "2026-02-13T10:30:00Z"
    }
  ]
}
```

**Notes**:

- Unlike the public browsing endpoint, this includes INACTIVE products

---

##### 3. Get Product by ID

Retrieve a specific product by its unique identifier.

**Endpoint**: `GET /items/products/{uid}`

**Path Parameters**:

- `uid`: UUID - Product identifier

**Response**:

```json
{
  "uid": "824345c4-6ef5-410d-87d3-2ec7d4a5278b",
  "name": "Espresso",
  "price": "3.50",
  "itemStatus": "ACTIVE",
  "createdAt": "2026-02-13T10:30:00Z",
  "updatedAt": "2026-02-13T10:30:00Z"
}
```

---

##### 4. Update Product

Update an existing product's details. The itemStatus can be switched between ACTIVE and INACTIVE to control availability
without deleting the record.

**Endpoint**: `PUT /items/products/{uid}`

**Path Parameters**:

- `uid`: UUID - Product identifier

**Request Body**:

```json
{
  "name": "Espresso",
  "price": "3.75",
  "itemStatus": "ACTIVE"
}
```

**Response**:

```json
{
  "uid": "824345c4-6ef5-410d-87d3-2ec7d4a5278b",
  "name": "Espresso",
  "price": "3.75",
  "itemStatus": "ACTIVE",
  "createdAt": "2026-02-13T10:30:00Z",
  "updatedAt": "2026-02-13T15:45:00Z"
}
```

**Notes**:

- Use this endpoint to mark products as INACTIVE instead of deleting them if they are linked to orders or carts
- Two products with the same name are not allowed

---

##### 5. Delete Product

Delete a product from the catalog.

**Endpoint**: `DELETE /items/products/{uid}`

**Path Parameters**:

- `uid`: UUID - Product identifier

**Response**: Empty body with 200 OK

**Notes**:

- This endpoint will fail if the product has associated orders or is in any cart
- In such cases, use the update endpoint to mark the product as INACTIVE instead

---

### Reporting API

Generate business intelligence reports. All endpoints are prefixed with `/reports`.

##### 1. Most Used Toppings Per Product

Generate a report showing the most frequently used toppings for each product.

**Endpoint**: `GET /reports/toppings/most-used`

**Response**:

```json
{
  "productToppingCounts": [
    {
      "productUid": "824345c4-6ef5-410d-87d3-2ec7d4a5278b",
      "productName": "Espresso",
      "totalOrdered": 150,
      "toppingCounts": [
        {
          "toppingUid": "c05d573d-9bc1-4915-be90-6fe6416bef99",
          "toppingName": "Extra Shot",
          "totalOrderedForProduct": 120,
          "averageOrderedForProduct": 0.8
        },
        {
          "toppingUid": "660e8400-e29b-41d4-a716-446655440001",
          "toppingName": "Whipped Cream",
          "totalOrderedForProduct": 45,
          "averageOrderedForProduct": 0.3
        }
      ]
    },
    {
      "productUid": "550e8400-e29b-41d4-a716-446655440001",
      "productName": "Cappuccino",
      "totalOrdered": 200,
      "toppingCounts": [
        {
          "toppingUid": "660e8400-e29b-41d4-a716-446655440001",
          "toppingName": "Whipped Cream",
          "totalOrderedForProduct": 180,
          "averageOrderedForProduct": 0.9
        },
        {
          "toppingUid": "c05d573d-9bc1-4915-be90-6fe6416bef99",
          "toppingName": "Extra Shot",
          "totalOrderedForProduct": 60,
          "averageOrderedForProduct": 0.3
        }
      ]
    }
  ]
}
```

**Notes**:

- This report helps identify popular topping combinations
- Shows the total amount of times a product has been orderedd
- Shows the total amount of times each topping has been ordered for that product has been ordered (flattened, total does
  not need to be multiplied by product order quantity)
- Shows the average amount of times the topping gets ordered for product
- Can be used in reporting most frequently used toppings. The averages show the frequency of ordering while the absolute
  values show scale.
- Average and absolute values are useful for making business decisions (a very frequently ordered topping is not
  valuable information if order counts are very low)
- Data is based on completed orders

---

## Testing Implementation

The tests in this project mainly revolve around testing core module logic. As the bulk of the logic is basic CRUD, with
pass-through service method calls, a large portion of tests are database interaction tests + integration tests for
controllers.
Service unit tests are used when more complex logic is involved, such as in the discount calculation.

In addition, end-to-end tests are implemented to ensure the API and core services can communicate as expected. A couple
of basic scenarios for customer journeys are added, with more to be added in the future, along with admin scenarios.

---

## TODO

Due to the nature of the toy application, many features and improvements have been left out for completion.

Here I list these TODOs which would be done had this been a production application.

### High Priority

- [ ] Fill out remaining tests
- [ ] Have each endpoint in API services tested for e2e calls (can be in cucumber tests)
- [ ] Implement proper api error handling and validation messages
- [ ] Add API object validation and respond predictably to invalid input (including user header)
- [ ] Add pagination and sorting to list endpoints for consistent response ordering and bounded sizes
- [ ] Add database indices
- [ ] Update cart item addition logic to return added cart item info in the response
- [ ] Add typed uuids to improve type safety
- [ ] Make floating point scales in API and db consistent between entities
- [ ] Update discount handlers to use thresholds that come from application properties
- [ ] Add test for existing order/order item prices not changing due to product/topping edits
- [ ] Other todo's listed in the code
- [ ] Add tests as part of the build job on pull requests to validate changes are not breaking

### Data & Analytics

- [ ] Add history tables for management actions
- [ ] Add history tables for cart actions

### Robustness

- [ ] Use time provider for timestamp logic

### Documentation

- [ ] Move to swagger

### Testing

- [ ] Use builders to persist data in integration tests instead of direct repository calls for better readability and
  maintainability
- [ ] Test database enums match java enums
- [ ] Use timeProvider (also improves configurability and reliability)

---

## Roadmap

- [ ] Improve discount logic to show which product was discounted for the large order discount
- [ ] Warn users when the price of an item changes while it is in their cart
- [ ] Add get endpoint for cart product item
- [ ] Add update for cart product item (to change quantity and toppings)
- [ ] Add delete for cart product item

