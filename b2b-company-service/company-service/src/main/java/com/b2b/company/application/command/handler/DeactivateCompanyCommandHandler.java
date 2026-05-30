package com.b2b.company.application.command.handler;

import com.b2b.company.application.command.dto.ChangeCompanyStatusResult;
import com.b2b.company.application.command.dto.DeactivateCompanyCommand;
import com.b2b.company.application.exception.CompanyNotFoundException;
import com.b2b.company.application.port.in.DeactivateCompanyUseCase;
import com.b2b.company.application.port.out.CompanyRepositoryPort;
import com.b2b.company.domain.model.Company;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeactivateCompanyCommandHandler implements DeactivateCompanyUseCase {

    private final CompanyRepositoryPort companyRepositoryPort;

    public DeactivateCompanyCommandHandler(CompanyRepositoryPort companyRepositoryPort) {
        this.companyRepositoryPort = companyRepositoryPort;
    }

    @Override
    @Transactional
    public ChangeCompanyStatusResult handle(DeactivateCompanyCommand command) {
        Company company = companyRepositoryPort.findById(command.id())
                .orElseThrow(() -> new CompanyNotFoundException(
                        "Company not found with id: " + command.id()
                ));

        company.deactivate();

        Company savedCompany = companyRepositoryPort.save(company);

        return new ChangeCompanyStatusResult(
                savedCompany.getId(),
                savedCompany.getName(),
                savedCompany.getTaxNumber().getValue(),
                savedCompany.getCompanyType(),
                savedCompany.getStatus(),
                savedCompany.getCreatedAt()
        );
    }
}