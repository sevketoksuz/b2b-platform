package com.b2b.company.application.query.handler;

import com.b2b.company.application.exception.CompanyMemberNotFoundException;
import com.b2b.company.application.port.in.GetCompanyMemberByIdUseCase;
import com.b2b.company.application.port.out.CompanyMemberRepositoryPort;
import com.b2b.company.application.query.dto.GetCompanyMemberByIdQuery;
import com.b2b.company.application.query.dto.GetCompanyMemberByIdResult;
import com.b2b.company.domain.model.CompanyMember;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetCompanyMemberByIdQueryHandler implements GetCompanyMemberByIdUseCase {

    private final CompanyMemberRepositoryPort companyMemberRepositoryPort;

    public GetCompanyMemberByIdQueryHandler(
            CompanyMemberRepositoryPort companyMemberRepositoryPort
    ) {
        this.companyMemberRepositoryPort = companyMemberRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public GetCompanyMemberByIdResult handle(GetCompanyMemberByIdQuery query) {
        CompanyMember member = companyMemberRepositoryPort.findById(query.id())
                .orElseThrow(() -> new CompanyMemberNotFoundException(
                        "Company member not found with id: " + query.id()
                ));

        return new GetCompanyMemberByIdResult(
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