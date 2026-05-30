package com.b2b.company.api.mapper;

import com.b2b.company.api.request.CreateCompanyRequest;
import com.b2b.company.api.response.CompanyListItemResponse;
import com.b2b.company.api.response.CompanyResponse;
import com.b2b.company.api.response.PagedResponse;
import com.b2b.company.application.command.dto.CreateCompanyCommand;
import com.b2b.company.application.command.dto.CreateCompanyResult;
import com.b2b.company.application.query.dto.GetCompaniesResult;
import com.b2b.company.application.query.dto.GetCompanyByIdResult;
import com.b2b.company.api.request.UpdateCompanyRequest;
import com.b2b.company.application.command.dto.UpdateCompanyCommand;
import com.b2b.company.application.command.dto.UpdateCompanyResult;
import com.b2b.company.application.command.dto.ChangeCompanyStatusResult;

import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.List;

@Component
public class CompanyApiMapper {

    public CreateCompanyCommand toCommand(CreateCompanyRequest request) {
        return new CreateCompanyCommand(
                request.name(),
                request.taxNumber(),
                request.companyType()
        );
    }

    public CompanyResponse toResponse(CreateCompanyResult result) {
        return new CompanyResponse(
                result.id(),
                result.name(),
                result.taxNumber(),
                result.companyType(),
                result.status(),
                result.createdAt()
        );
    }

    public CompanyResponse toResponse(GetCompanyByIdResult result) {
        return new CompanyResponse(
                result.id(),
                result.name(),
                result.taxNumber(),
                result.companyType(),
                result.status(),
                result.createdAt()
        );
    }

    public PagedResponse<CompanyListItemResponse> toResponse(GetCompaniesResult result) {
        List<CompanyListItemResponse> content = result.content()
                .stream()
                .map(company -> new CompanyListItemResponse(
                        company.id(),
                        company.name(),
                        company.taxNumber(),
                        company.companyType(),
                        company.status(),
                        company.createdAt()
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

    public UpdateCompanyCommand toCommand(UUID id, UpdateCompanyRequest request) {
        return new UpdateCompanyCommand(
                id,
                request.name(),
                request.taxNumber(),
                request.companyType()
        );
    }

    public CompanyResponse toResponse(UpdateCompanyResult result) {
        return new CompanyResponse(
                result.id(),
                result.name(),
                result.taxNumber(),
                result.companyType(),
                result.status(),
                result.createdAt()
        );
    }

    public CompanyResponse toResponse(ChangeCompanyStatusResult result) {
        return new CompanyResponse(
                result.id(),
                result.name(),
                result.taxNumber(),
                result.companyType(),
                result.status(),
                result.createdAt()
        );
    }
}