package com.b2b.company.api.controller;

import com.b2b.company.api.mapper.CompanyApiMapper;
import com.b2b.company.api.request.CreateCompanyRequest;
import com.b2b.company.api.response.CompanyListItemResponse;
import com.b2b.company.api.response.CompanyResponse;
import com.b2b.company.api.response.PagedResponse;
import com.b2b.company.application.command.dto.CreateCompanyCommand;
import com.b2b.company.application.command.dto.CreateCompanyResult;
import com.b2b.company.application.port.in.CreateCompanyUseCase;
import com.b2b.company.application.port.in.GetCompaniesUseCase;
import com.b2b.company.application.port.in.GetCompanyByIdUseCase;
import com.b2b.company.application.query.dto.GetCompaniesQuery;
import com.b2b.company.application.query.dto.GetCompaniesResult;
import com.b2b.company.application.query.dto.GetCompanyByIdQuery;
import com.b2b.company.application.query.dto.GetCompanyByIdResult;
import com.b2b.company.domain.enumtype.CompanyStatus;
import com.b2b.company.domain.enumtype.CompanyType;
import com.b2b.company.api.request.UpdateCompanyRequest;
import com.b2b.company.application.command.dto.UpdateCompanyCommand;
import com.b2b.company.application.command.dto.UpdateCompanyResult;
import com.b2b.company.application.port.in.UpdateCompanyUseCase;
import com.b2b.company.application.command.dto.ActivateCompanyCommand;
import com.b2b.company.application.command.dto.ChangeCompanyStatusResult;
import com.b2b.company.application.command.dto.DeactivateCompanyCommand;
import com.b2b.company.application.port.in.ActivateCompanyUseCase;
import com.b2b.company.application.port.in.DeactivateCompanyUseCase;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CreateCompanyUseCase createCompanyUseCase;
    private final GetCompanyByIdUseCase getCompanyByIdUseCase;
    private final GetCompaniesUseCase getCompaniesUseCase;
    private final CompanyApiMapper companyApiMapper;
    private final UpdateCompanyUseCase updateCompanyUseCase;
    private final ActivateCompanyUseCase activateCompanyUseCase;
    private final DeactivateCompanyUseCase deactivateCompanyUseCase;

    public CompanyController(
            CreateCompanyUseCase createCompanyUseCase,
            GetCompanyByIdUseCase getCompanyByIdUseCase,
            GetCompaniesUseCase getCompaniesUseCase,
            UpdateCompanyUseCase updateCompanyUseCase,
            CompanyApiMapper companyApiMapper,
            ActivateCompanyUseCase activateCompanyUseCase,
            DeactivateCompanyUseCase deactivateCompanyUseCase
    ) {
        this.createCompanyUseCase = createCompanyUseCase;
        this.getCompanyByIdUseCase = getCompanyByIdUseCase;
        this.getCompaniesUseCase = getCompaniesUseCase;
        this.updateCompanyUseCase = updateCompanyUseCase;
        this.companyApiMapper = companyApiMapper;
        this.activateCompanyUseCase = activateCompanyUseCase;
        this.deactivateCompanyUseCase = deactivateCompanyUseCase;
    }

    @PostMapping
    public ResponseEntity<CompanyResponse> createCompany(
            @Valid @RequestBody CreateCompanyRequest request
    ) {
        CreateCompanyCommand command = companyApiMapper.toCommand(request);

        CreateCompanyResult result = createCompanyUseCase.handle(command);

        CompanyResponse response = companyApiMapper.toResponse(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getCompanyById(
            @PathVariable UUID id
    ) {
        GetCompanyByIdQuery query = new GetCompanyByIdQuery(id);

        GetCompanyByIdResult result = getCompanyByIdUseCase.handle(query);

        CompanyResponse response = companyApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<CompanyListItemResponse>> getCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) CompanyType companyType,
            @RequestParam(required = false) CompanyStatus status,
            @RequestParam(required = false) String search
    ) {
        GetCompaniesQuery query = new GetCompaniesQuery(
                page,
                size,
                companyType,
                status,
                search
        );

        GetCompaniesResult result = getCompaniesUseCase.handle(query);

        PagedResponse<CompanyListItemResponse> response = companyApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CompanyResponse> updateCompany(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCompanyRequest request
    ) {
        UpdateCompanyCommand command = companyApiMapper.toCommand(id, request);

        UpdateCompanyResult result = updateCompanyUseCase.handle(command);

        CompanyResponse response = companyApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<CompanyResponse> deactivateCompany(
            @PathVariable UUID id
    ) {
        DeactivateCompanyCommand command = new DeactivateCompanyCommand(id);

        ChangeCompanyStatusResult result = deactivateCompanyUseCase.handle(command);

        CompanyResponse response = companyApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<CompanyResponse> activateCompany(
            @PathVariable UUID id
    ) {
        ActivateCompanyCommand command = new ActivateCompanyCommand(id);

        ChangeCompanyStatusResult result = activateCompanyUseCase.handle(command);

        CompanyResponse response = companyApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }
}