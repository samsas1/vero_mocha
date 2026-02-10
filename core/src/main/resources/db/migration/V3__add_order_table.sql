CREATE TYPE order_status AS ENUM ('PLACED', 'FULFILLED', 'CANCELLED');

CREATE TABLE customer_order
(
    sid            SERIAL PRIMARY KEY,
    uid            UUID UNIQUE  NOT NULL,
    user_uid       UUID UNIQUE  NOT NULL,
    order_status   order_status NOT NULL DEFAULT 'PLACED',
    original_price NUMERIC      NOT NULL,
    final_price    NUMERIC      NOT NULL,
    created_at     timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customer_order_product_item
(
    sid                        SERIAL PRIMARY KEY,
    uid                        UUID UNIQUE NOT NULL,
    order_sid                  INT         NOT NULL REFERENCES customer_order (sid),
    product_sid                INT         NOT NULL REFERENCES product (sid),
    original_price_per_product NUMERIC     NOT NULL,
    quantity                   integer     NOT NULL CHECK (quantity > 0),
    created_at                 timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                 timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customer_order_topping_item
(
    sid                         SERIAL PRIMARY KEY,
    uid                         UUID UNIQUE NOT NULL,
    customer_order_product_item INT         NOT NULL REFERENCES customer_order_product_item (sid),
    topping_sid                 INT         NOT NULL REFERENCES topping (sid),
    original_price_per_topping  NUMERIC     NOT NULL,
    quantity                    integer     NOT NULL CHECK (quantity > 0),
    created_at                  timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP
);