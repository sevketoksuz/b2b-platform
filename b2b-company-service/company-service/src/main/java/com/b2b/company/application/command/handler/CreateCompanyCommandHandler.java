package com.b2b.company.application.command.handler;

import com.b2b.company.application.command.dto.CreateCompanyCommand;
import com.b2b.company.application.command.dto.CreateCompanyResult;
import com.b2b.company.application.exception.CompanyAlreadyExistsException;
import com.b2b.company.application.port.in.CreateCompanyUseCase;
import com.b2b.company.application.port.out.CompanyRepositoryPort;
import com.b2b.company.domain.model.Company;
import com.b2b.company.domain.valueobject.TaxNumber;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateCompanyCommandHandler implements CreateCompanyUseCase {

    private final CompanyRepositoryPort companyRepositoryPort;

    public CreateCompanyCommandHandler(CompanyRepositoryPort companyRepositoryPort) {
        this.companyRepositoryPort = companyRepositoryPort;
    }

    @Override
    @Transactional
    public CreateCompanyResult handle(CreateCompanyCommand command) {
        TaxNumber taxNumber = TaxNumber.of(command.taxNumber());

        if (companyRepositoryPort.existsByTaxNumber(taxNumber)) {
            throw new CompanyAlreadyExistsException("Company already exists with tax number: " + taxNumber.getValue());
        }

        Company company = Company.create(
                command.name(),
                taxNumber,
                command.companyType()
        );

        Company savedCompany = companyRepositoryPort.save(company);

        return new CreateCompanyResult(
                savedCompany.getId(),
                savedCompany.getName(),
                savedCompany.getTaxNumber().getValue(),
                savedCompany.getCompanyType(),
                savedCompany.getStatus(),
                savedCompany.getCreatedAt()
        );
    }
}