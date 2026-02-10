CREATE TYPE order_status AS ENUM ('PLACED', 'FULFILLED', 'CANCELLED');
CREATE TYPE discount_type AS ENUM ('NO_DISCOUNT','FULL_CART', 'FREE_ITEM_FOR_LARGE_ORDER');

CREATE TABLE customer_order
(
    sid            SERIAL PRIMARY KEY,
    uid            UUID UNIQUE   NOT NULL,
    user_uid       UUID UNIQUE   NOT NULL,
    order_status   order_status  NOT NULL DEFAULT 'PLACED',
    discount_type  discount_type NOT NULL DEFAULT 'NO_DISCOUNT',
    original_price NUMERIC       NOT NULL CHECK (original_price >= 0),
    -- Assuming taxes are calculated in original price and shipping is free
    -- Would need to relax this constraint otherwise
    final_price    NUMERIC       NOT NULL CHECK (final_price >= original_price),
    created_at     timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customer_order_product_item
(
    sid                        SERIAL PRIMARY KEY,
    uid                        UUID UNIQUE NOT NULL,
    customer_order_sid         INT         NOT NULL REFERENCES customer_order (sid),
    product_sid                INT         NOT NULL REFERENCES product (sid),
    original_price_per_product NUMERIC     NOT NULL CHECK (original_price_per_product >= 0),
    quantity                   integer     NOT NULL CHECK (quantity > 0),
    created_at                 timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                 timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customer_order_topping_item
(
    sid                             SERIAL PRIMARY KEY,
    uid                             UUID UNIQUE NOT NULL,
    customer_order_product_item_sid INT         NOT NULL REFERENCES customer_order_product_item (sid),
    topping_sid                     INT         NOT NULL REFERENCES topping (sid),
    original_price_per_topping      NUMERIC     NOT NULL CHECK (original_price_per_topping >= 0),
    quantity                        integer     NOT NULL CHECK (quantity > 0),
    created_at                      timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                      timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP
);