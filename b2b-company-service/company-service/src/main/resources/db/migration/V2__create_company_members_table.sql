CREATE TABLE company_members (
                                 id UUID PRIMARY KEY,
                                 company_id UUID NOT NULL,
                                 full_name VARCHAR(100) NOT NULL,
                                 email VARCHAR(150) NOT NULL,
                                 role VARCHAR(30) NOT NULL,
                                 status VARCHAR(30) NOT NULL,
                                 created_at TIMESTAMP NOT NULL,
                                 updated_at TIMESTAMP NOT NULL,

                                 CONSTRAINT fk_company_members_company
                                     FOREIGN KEY (company_id)
                                         REFERENCES companies(id),

                                 CONSTRAINT uq_company_members_company_email
                                     UNIQUE (company_id, email)
);

CREATE INDEX idx_company_members_company_id ON company_members(company_id);
CREATE INDEX idx_company_members_email ON company_members(email);
CREATE INDEX idx_company_members_status ON company_members(status);
CREATE INDEX idx_company_members_role ON company_members(role);