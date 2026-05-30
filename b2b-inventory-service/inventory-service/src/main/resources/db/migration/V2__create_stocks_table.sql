CREATE TABLE stocks (
                        id UUID PRIMARY KEY,
                        company_id UUID NOT NULL,
                        product_id UUID NOT NULL,
                        quantity NUMERIC(19, 2) NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP NOT NULL,

                        CONSTRAINT uq_stocks_company_product UNIQUE (company_id, product_id),

                        CONSTRAINT fk_stocks_product
                            FOREIGN KEY (product_id)
                                REFERENCES products(id),

                        CONSTRAINT chk_stocks_quantity_non_negative
                            CHECK (quantity >= 0)
);

CREATE INDEX idx_stocks_company_id ON stocks(company_id);
CREATE INDEX idx_stocks_product_id ON stocks(product_id);