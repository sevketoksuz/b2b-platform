CREATE TABLE companies (
                           id UUID PRIMARY KEY,
                           name VARCHAR(150) NOT NULL,
                           tax_number VARCHAR(11) NOT NULL UNIQUE,
                           company_type VARCHAR(30) NOT NULL,
                           status VARCHAR(30) NOT NULL,
                           created_at TIMESTAMP NOT NULL,
                           updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_companies_tax_number ON companies(tax_number);
CREATE INDEX idx_companies_status ON companies(status);
CREATE INDEX idx_companies_company_type ON companies(company_type);