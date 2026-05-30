CREATE TABLE stock_movements (
                                 id UUID PRIMARY KEY,
                                 company_id UUID NOT NULL,
                                 product_id UUID NOT NULL,
                                 movement_type VARCHAR(30) NOT NULL,
                                 quantity NUMERIC(19, 2) NOT NULL,
                                 previous_quantity NUMERIC(19, 2) NOT NULL,
                                 new_quantity NUMERIC(19, 2) NOT NULL,
                                 reason VARCHAR(255),
                                 created_at TIMESTAMP NOT NULL,

                                 CONSTRAINT fk_stock_movements_product
                                     FOREIGN KEY (product_id)
                                         REFERENCES products(id)
);

CREATE INDEX idx_stock_movements_company_id ON stock_movements(company_id);
CREATE INDEX idx_stock_movements_product_id ON stock_movements(product_id);
CREATE INDEX idx_stock_movements_movement_type ON stock_movements(movement_type);
CREATE INDEX idx_stock_movements_created_at ON stock_movements(created_at);