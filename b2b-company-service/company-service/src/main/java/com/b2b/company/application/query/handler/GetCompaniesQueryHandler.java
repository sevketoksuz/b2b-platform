package com.b2b.company.application.query.handler;

import com.b2b.company.application.port.in.GetCompaniesUseCase;
import com.b2b.company.application.port.out.CompanyRepositoryPort;
import com.b2b.company.application.port.out.CompanySearchCriteria;
import com.b2b.company.application.port.out.PagedResult;
import com.b2b.company.application.query.dto.CompanyListItemResult;
import com.b2b.company.application.query.dto.GetCompaniesQuery;
import com.b2b.company.application.query.dto.GetCompaniesResult;
import com.b2b.company.domain.model.Company;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetCompaniesQueryHandler implements GetCompaniesUseCase {

    private final CompanyRepositoryPort companyRepositoryPort;

    public GetCompaniesQueryHandler(CompanyRepositoryPort companyRepositoryPort) {
        this.companyRepositoryPort = companyRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public GetCompaniesResult handle(GetCompaniesQuery query) {
        validatePagination(query.page(), query.size());

        CompanySearchCriteria criteria = new CompanySearchCriteria(
                query.page(),
                query.size(),
                query.companyType(),
                query.status(),
                query.search()
        );

        PagedResult<Company> pagedCompanies = companyRepositoryPort.findCompanies(criteria);

        List<CompanyListItemResult> content = pagedCompanies.content()
                .stream()
                .map(this::toListItemResult)
                .toList();

        return new GetCompaniesResult(
                content,
                pagedCompanies.page(),
                pagedCompanies.size(),
                pagedCompanies.totalElements(),
                pagedCompanies.totalPages()
        );
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative.");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Size must be between 1 and 100.");
        }
    }

    private CompanyListItemResult toListItemResult(Company company) {
        return new CompanyListItemResult(
                company.getId(),
                company.getName(),
                company.getTaxNumber().getValue(),
                company.getCompanyType(),
                company.getStatus(),
                company.getCreatedAt()
        );
    }
}