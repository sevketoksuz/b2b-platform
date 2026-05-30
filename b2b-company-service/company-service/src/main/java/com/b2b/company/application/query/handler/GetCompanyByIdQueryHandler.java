package com.b2b.company.application.query.handler;

import com.b2b.company.application.exception.CompanyNotFoundException;
import com.b2b.company.application.port.in.GetCompanyByIdUseCase;
import com.b2b.company.application.port.out.CompanyRepositoryPort;
import com.b2b.company.application.query.dto.GetCompanyByIdQuery;
import com.b2b.company.application.query.dto.GetCompanyByIdResult;
import com.b2b.company.domain.model.Company;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetCompanyByIdQueryHandler implements GetCompanyByIdUseCase {

    private final CompanyRepositoryPort companyRepositoryPort;

    public GetCompanyByIdQueryHandler(CompanyRepositoryPort companyRepositoryPort) {
        this.companyRepositoryPort = companyRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public GetCompanyByIdResult handle(GetCompanyByIdQuery query) {
        Company company = companyRepositoryPort.findById(query.id())
                .orElseThrow(() -> new CompanyNotFoundException(
                        "Company not found with id: " + query.id()
                ));

        return new GetCompanyByIdResult(
                company.getId(),
                company.getName(),
                company.getTaxNumber().getValue(),
                company.getCompanyType(),
                company.getStatus(),
                company.getCreatedAt()
        );
    }
}