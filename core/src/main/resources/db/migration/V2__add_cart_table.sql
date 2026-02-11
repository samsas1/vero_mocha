CREATE TABLE IF NOT EXISTS cart
(
    sid        SERIAL PRIMARY KEY,
    uid        UUID UNIQUE NOT NULL,
    user_uid   UUID UNIQUE NOT NULL, -- Assuming one cart per user
    created_at timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- For toy application purposes, would not be included in production
-- Furthermore, in a real application there would be a user table, this is slightly denormalized
INSERT INTO cart (uid, user_uid)
VALUES (gen_random_uuid(),
        '7ad5bc4e-0de9-41dc-a5b6-745c1debba23');

CREATE TABLE IF NOT EXISTS cart_product_item
(
    sid         SERIAL PRIMARY KEY,
    uid         UUID UNIQUE NOT NULL,
    cart_sid    INT         NOT NULL REFERENCES cart (sid) ON DELETE CASCADE,
    product_sid INT         NOT NULL REFERENCES product (sid),
    quantity    integer     NOT NULL CHECK (quantity > 0),
    created_at  timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cart_topping_item
(
    sid                   SERIAL PRIMARY KEY,
    uid                   UUID UNIQUE NOT NULL,
    cart_product_item_sid INT         NOT NULL REFERENCES cart_product_item (sid) ON DELETE CASCADE,
    topping_sid           INT         NOT NULL REFERENCES topping (sid),
    quantity              integer     NOT NULL CHECK (quantity > 0),
    created_at            timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Ensure that multiple repeats of toppings are calculated via quantity, not multiple rows
ALTER TABLE cart_topping_item
    ADD CONSTRAINT unique_cart_product_item_topping_cart_topping_item UNIQUE (cart_product_item_sid, topping_sid);