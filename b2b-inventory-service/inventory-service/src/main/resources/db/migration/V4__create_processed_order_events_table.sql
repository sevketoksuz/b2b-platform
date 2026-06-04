CREATE TABLE processed_order_events (
                                        id UUID PRIMARY KEY,
                                        event_type VARCHAR(80) NOT NULL,
                                        order_id UUID NOT NULL,
                                        product_id UUID NOT NULL,
                                        processed_at TIMESTAMP NOT NULL,

                                        CONSTRAINT uq_processed_order_events_type_order_product
                                            UNIQUE (event_type, order_id, product_id)
);

CREATE INDEX idx_processed_order_events_order_id
    ON processed_order_events(order_id);

CREATE INDEX idx_processed_order_events_product_id
    ON processed_order_events(product_id);

CREATE INDEX idx_processed_order_events_processed_at
    ON processed_order_events(processed_at);