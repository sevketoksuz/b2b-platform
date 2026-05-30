package com.b2b.company.application.command.handler;

import com.b2b.company.application.command.dto.UpdateCompanyCommand;
import com.b2b.company.application.command.dto.UpdateCompanyResult;
import com.b2b.company.application.exception.CompanyAlreadyExistsException;
import com.b2b.company.application.exception.CompanyNotFoundException;
import com.b2b.company.application.port.in.UpdateCompanyUseCase;
import com.b2b.company.application.port.out.CompanyRepositoryPort;
import com.b2b.company.domain.model.Company;
import com.b2b.company.domain.valueobject.TaxNumber;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateCompanyCommandHandler implements UpdateCompanyUseCase {

    private final CompanyRepositoryPort companyRepositoryPort;

    public UpdateCompanyCommandHandler(CompanyRepositoryPort companyRepositoryPort) {
        this.companyRepositoryPort = companyRepositoryPort;
    }

    @Override
    @Transactional
    public UpdateCompanyResult handle(UpdateCompanyCommand command) {
        Company company = companyRepositoryPort.findById(command.id())
                .orElseThrow(() -> new CompanyNotFoundException(
                        "Company not found with id: " + command.id()
                ));

        TaxNumber taxNumber = TaxNumber.of(command.taxNumber());

        if (companyRepositoryPort.existsByTaxNumberAndIdNot(taxNumber, command.id())) {
            throw new CompanyAlreadyExistsException(
                    "Another company already exists with tax number: " + taxNumber.getValue()
            );
        }

        company.updateDetails(
                command.name(),
                taxNumber,
                command.companyType()
        );

        Company savedCompany = companyRepositoryPort.save(company);

        return new UpdateCompanyResult(
                savedCompany.getId(),
                savedCompany.getName(),
                savedCompany.getTaxNumber().getValue(),
                savedCompany.getCompanyType(),
                savedCompany.getStatus(),
                savedCompany.getCreatedAt()
        );
    }
}