CREATE TYPE item_status AS ENUM ('ACTIVE', 'INACTIVE');

CREATE TABLE product
(
    sid   SERIAL PRIMARY KEY,
    uid   UUID UNIQUE NOT NULL,
    name  text UNIQUE NOT NULL,
    price numeric     NOT NULL,
    status item_status NOT NULL DEFAULT 'ACTIVE',
    created_at timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE topping
(
    sid   SERIAL PRIMARY KEY,
    uid   UUID UNIQUE NOT NULL,
    name  text        NOT NULL,
    price numeric     NOT NULL,
    status item_status NOT NULL DEFAULT 'ACTIVE',
    created_at timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP
);
