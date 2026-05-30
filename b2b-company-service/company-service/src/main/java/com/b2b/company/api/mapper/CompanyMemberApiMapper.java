package com.b2b.company.api.mapper;

import com.b2b.company.api.request.CreateCompanyMemberRequest;
import com.b2b.company.api.response.CompanyMemberResponse;
import com.b2b.company.application.command.dto.CreateCompanyMemberCommand;
import com.b2b.company.application.command.dto.CreateCompanyMemberResult;
import com.b2b.company.api.response.CompanyMemberListItemResponse;
import com.b2b.company.api.response.PagedResponse;
import com.b2b.company.application.query.dto.GetCompanyMembersResult;
import com.b2b.company.application.query.dto.GetCompanyMemberByIdResult;
import com.b2b.company.api.request.UpdateCompanyMemberRequest;
import com.b2b.company.application.command.dto.UpdateCompanyMemberCommand;
import com.b2b.company.application.command.dto.UpdateCompanyMemberResult;
import com.b2b.company.application.command.dto.ChangeCompanyMemberStatusResult;

import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.List;

@Component
public class CompanyMemberApiMapper {

    public CreateCompanyMemberCommand toCommand(
            UUID companyId,
            CreateCompanyMemberRequest request
    ) {
        return new CreateCompanyMemberCommand(
                companyId,
                request.fullName(),
                request.email(),
                request.role()
        );
    }

    public CompanyMemberResponse toResponse(CreateCompanyMemberResult result) {
        return new CompanyMemberResponse(
                result.id(),
                result.companyId(),
                result.fullName(),
                result.email(),
                result.role(),
                result.status(),
                result.createdAt()
        );
    }

    public PagedResponse<CompanyMemberListItemResponse> toResponse(GetCompanyMembersResult result) {
        List<CompanyMemberListItemResponse> content = result.content()
                .stream()
                .map(member -> new CompanyMemberListItemResponse(
                        member.id(),
                        member.companyId(),
                        member.fullName(),
                        member.email(),
                        member.role(),
                        member.status(),
                        member.createdAt()
                ))
                .toList();

        return new PagedResponse<>(
                content,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }

    public CompanyMemberResponse toResponse(GetCompanyMemberByIdResult result) {
        return new CompanyMemberResponse(
                result.id(),
                result.companyId(),
                result.fullName(),
                result.email(),
                result.role(),
                result.status(),
                result.createdAt()
        );
    }

    public UpdateCompanyMemberCommand toCommand(
            UUID id,
            UpdateCompanyMemberRequest request
    ) {
        return new UpdateCompanyMemberCommand(
                id,
                request.fullName(),
                request.email(),
                request.role()
        );
    }

    public CompanyMemberResponse toResponse(UpdateCompanyMemberResult result) {
        return new CompanyMemberResponse(
                result.id(),
                result.companyId(),
                result.fullName(),
                result.email(),
                result.role(),
                result.status(),
                result.createdAt()
        );
    }

    public CompanyMemberResponse toResponse(ChangeCompanyMemberStatusResult result) {
        return new CompanyMemberResponse(
                result.id(),
                result.companyId(),
                result.fullName(),
                result.email(),
                result.role(),
                result.status(),
                result.createdAt()
        );
    }

}