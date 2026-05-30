package com.b2b.company.application.query.handler;

import com.b2b.company.application.exception.CompanyNotFoundException;
import com.b2b.company.application.port.in.GetCompanyMembersUseCase;
import com.b2b.company.application.port.out.CompanyMemberRepositoryPort;
import com.b2b.company.application.port.out.CompanyMemberSearchCriteria;
import com.b2b.company.application.port.out.CompanyRepositoryPort;
import com.b2b.company.application.port.out.PagedResult;
import com.b2b.company.application.query.dto.CompanyMemberListItemResult;
import com.b2b.company.application.query.dto.GetCompanyMembersQuery;
import com.b2b.company.application.query.dto.GetCompanyMembersResult;
import com.b2b.company.domain.model.CompanyMember;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetCompanyMembersQueryHandler implements GetCompanyMembersUseCase {

    private final CompanyRepositoryPort companyRepositoryPort;
    private final CompanyMemberRepositoryPort companyMemberRepositoryPort;

    public GetCompanyMembersQueryHandler(
            CompanyRepositoryPort companyRepositoryPort,
            CompanyMemberRepositoryPort companyMemberRepositoryPort
    ) {
        this.companyRepositoryPort = companyRepositoryPort;
        this.companyMemberRepositoryPort = companyMemberRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public GetCompanyMembersResult handle(GetCompanyMembersQuery query) {
        validatePagination(query.page(), query.size());

        companyRepositoryPort.findById(query.companyId())
                .orElseThrow(() -> new CompanyNotFoundException(
                        "Company not found with id: " + query.companyId()
                ));

        CompanyMemberSearchCriteria criteria = new CompanyMemberSearchCriteria(
                query.companyId(),
                query.page(),
                query.size(),
                query.role(),
                query.status(),
                query.search()
        );

        PagedResult<CompanyMember> pagedMembers =
                companyMemberRepositoryPort.findCompanyMembers(criteria);

        List<CompanyMemberListItemResult> content = pagedMembers.content()
                .stream()
                .map(this::toListItemResult)
                .toList();

        return new GetCompanyMembersResult(
                content,
                pagedMembers.page(),
                pagedMembers.size(),
                pagedMembers.totalElements(),
                pagedMembers.totalPages()
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

    private CompanyMemberListItemResult toListItemResult(CompanyMember member) {
        return new CompanyMemberListItemResult(
                member.getId(),
                member.getCompanyId(),
                member.getFullName(),
                member.getEmail().getValue(),
                member.getRole(),
                member.getStatus(),
                member.getCreatedAt()
        );
    }
}