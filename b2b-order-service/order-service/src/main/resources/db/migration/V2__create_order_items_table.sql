CREATE TABLE order_items (
                             id UUID PRIMARY KEY,
                             order_id UUID NOT NULL,
                             product_id UUID NOT NULL,
                             product_name VARCHAR(150) NOT NULL,
                             quantity NUMERIC(19, 2) NOT NULL,
                             unit_price_amount NUMERIC(19, 2) NOT NULL,
                             unit_price_currency VARCHAR(3) NOT NULL,
                             line_total_amount NUMERIC(19, 2) NOT NULL,
                             line_total_currency VARCHAR(3) NOT NULL,

                             CONSTRAINT fk_order_items_order
                                 FOREIGN KEY (order_id)
                                     REFERENCES orders(id)
                                     ON DELETE CASCADE,

                             CONSTRAINT chk_order_items_quantity_positive
                                 CHECK (quantity > 0),

                             CONSTRAINT chk_order_items_unit_price_positive
                                 CHECK (unit_price_amount > 0),

                             CONSTRAINT chk_order_items_line_total_non_negative
                                 CHECK (line_total_amount >= 0),

                             CONSTRAINT uq_order_items_order_product
                                 UNIQUE (order_id, product_id)
);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);