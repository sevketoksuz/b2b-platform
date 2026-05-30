package com.b2b.company.application.port.out;

import com.b2b.company.domain.model.Company;
import com.b2b.company.domain.valueobject.TaxNumber;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepositoryPort {

    Company save(Company company);

    Optional<Company> findById(UUID id);

    boolean existsByTaxNumber(TaxNumber taxNumber);

    boolean existsByTaxNumberAndIdNot(TaxNumber taxNumber, UUID id);

    PagedResult<Company> findCompanies(CompanySearchCriteria criteria);
}