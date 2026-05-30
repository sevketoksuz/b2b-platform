CREATE TABLE orders (
                        id UUID PRIMARY KEY,
                        buyer_company_id UUID NOT NULL,
                        seller_company_id UUID NOT NULL,
                        status VARCHAR(30) NOT NULL,
                        total_amount NUMERIC(19, 2) NOT NULL,
                        currency VARCHAR(3) NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP NOT NULL,
                        confirmed_at TIMESTAMP,
                        cancelled_at TIMESTAMP,
                        completed_at TIMESTAMP,

                        CONSTRAINT chk_orders_total_amount_non_negative
                            CHECK (total_amount >= 0),

                        CONSTRAINT chk_orders_buyer_seller_different
                            CHECK (buyer_company_id <> seller_company_id)
);

CREATE INDEX idx_orders_buyer_company_id ON orders(buyer_company_id);
CREATE INDEX idx_orders_seller_company_id ON orders(seller_company_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);