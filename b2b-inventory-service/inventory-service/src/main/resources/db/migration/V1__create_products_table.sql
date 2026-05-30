CREATE TABLE products (
                          id UUID PRIMARY KEY,
                          company_id UUID NOT NULL,
                          sku VARCHAR(50) NOT NULL,
                          name VARCHAR(150) NOT NULL,
                          description VARCHAR(500),
                          unit VARCHAR(30) NOT NULL,
                          status VARCHAR(30) NOT NULL,
                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP NOT NULL,

                          CONSTRAINT uq_products_company_sku UNIQUE (company_id, sku)
);

CREATE INDEX idx_products_company_id ON products(company_id);
CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_unit ON products(unit);
CREATE INDEX idx_products_name ON products(name);