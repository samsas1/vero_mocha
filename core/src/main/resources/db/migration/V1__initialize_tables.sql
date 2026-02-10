CREATE TYPE item_status AS ENUM ('ACTIVE', 'INACTIVE');

CREATE TABLE product
(
    sid   SERIAL PRIMARY KEY,
    uid   UUID UNIQUE NOT NULL,
    name  text UNIQUE NOT NULL,
    price numeric     NOT NULL CONSTRAINT positive_price_product CHECK (price >= 0),
    status item_status NOT NULL DEFAULT 'ACTIVE',
    created_at timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE topping
(
    sid   SERIAL PRIMARY KEY,
    uid   UUID UNIQUE NOT NULL,
    name  text UNIQUE NOT NULL,
    price numeric     NOT NULL CONSTRAINT positive_price_topping CHECK (price >= 0),
    status item_status NOT NULL DEFAULT 'ACTIVE',
    created_at timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP
);
