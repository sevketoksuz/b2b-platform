package com.b2b.company.api.controller;

import com.b2b.company.api.mapper.CompanyMemberApiMapper;
import com.b2b.company.api.request.CreateCompanyMemberRequest;
import com.b2b.company.api.response.CompanyMemberResponse;
import com.b2b.company.application.command.dto.CreateCompanyMemberCommand;
import com.b2b.company.application.command.dto.CreateCompanyMemberResult;
import com.b2b.company.application.port.in.CreateCompanyMemberUseCase;
import com.b2b.company.api.response.CompanyMemberListItemResponse;
import com.b2b.company.api.response.PagedResponse;
import com.b2b.company.application.port.in.GetCompanyMembersUseCase;
import com.b2b.company.application.query.dto.GetCompanyMembersQuery;
import com.b2b.company.application.query.dto.GetCompanyMembersResult;
import com.b2b.company.domain.enumtype.CompanyMemberRole;
import com.b2b.company.domain.enumtype.CompanyMemberStatus;
import com.b2b.company.application.port.in.GetCompanyMemberByIdUseCase;
import com.b2b.company.application.query.dto.GetCompanyMemberByIdQuery;
import com.b2b.company.application.query.dto.GetCompanyMemberByIdResult;
import com.b2b.company.api.request.UpdateCompanyMemberRequest;
import com.b2b.company.application.command.dto.UpdateCompanyMemberCommand;
import com.b2b.company.application.command.dto.UpdateCompanyMemberResult;
import com.b2b.company.application.port.in.UpdateCompanyMemberUseCase;
import com.b2b.company.application.command.dto.ActivateCompanyMemberCommand;
import com.b2b.company.application.command.dto.ChangeCompanyMemberStatusResult;
import com.b2b.company.application.command.dto.DeactivateCompanyMemberCommand;
import com.b2b.company.application.port.in.ActivateCompanyMemberUseCase;
import com.b2b.company.application.port.in.DeactivateCompanyMemberUseCase;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class CompanyMemberController {

    private final CreateCompanyMemberUseCase createCompanyMemberUseCase;
    private final CompanyMemberApiMapper companyMemberApiMapper;
    private final GetCompanyMembersUseCase getCompanyMembersUseCase;
    private final GetCompanyMemberByIdUseCase getCompanyMemberByIdUseCase;
    private final UpdateCompanyMemberUseCase updateCompanyMemberUseCase;
    private final ActivateCompanyMemberUseCase activateCompanyMemberUseCase;
    private final DeactivateCompanyMemberUseCase deactivateCompanyMemberUseCase;

    public CompanyMemberController(
            CreateCompanyMemberUseCase createCompanyMemberUseCase,
            GetCompanyMembersUseCase getCompanyMembersUseCase,
            GetCompanyMemberByIdUseCase getCompanyMemberByIdUseCase,
            UpdateCompanyMemberUseCase updateCompanyMemberUseCase,
            ActivateCompanyMemberUseCase activateCompanyMemberUseCase,
            DeactivateCompanyMemberUseCase deactivateCompanyMemberUseCase,
            CompanyMemberApiMapper companyMemberApiMapper
    ) {
        this.createCompanyMemberUseCase = createCompanyMemberUseCase;
        this.getCompanyMembersUseCase = getCompanyMembersUseCase;
        this.getCompanyMemberByIdUseCase = getCompanyMemberByIdUseCase;
        this.updateCompanyMemberUseCase = updateCompanyMemberUseCase;
        this.activateCompanyMemberUseCase = activateCompanyMemberUseCase;
        this.deactivateCompanyMemberUseCase = deactivateCompanyMemberUseCase;
        this.companyMemberApiMapper = companyMemberApiMapper;
    }

    @PostMapping("/companies/{companyId}/members")
    public ResponseEntity<CompanyMemberResponse> createCompanyMember(
            @PathVariable UUID companyId,
            @Valid @RequestBody CreateCompanyMemberRequest request
    ) {
        CreateCompanyMemberCommand command = companyMemberApiMapper.toCommand(companyId, request);

        CreateCompanyMemberResult result = createCompanyMemberUseCase.handle(command);

        CompanyMemberResponse response = companyMemberApiMapper.toResponse(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/companies/{companyId}/members")
    public ResponseEntity<PagedResponse<CompanyMemberListItemResponse>> getCompanyMembers(
            @PathVariable UUID companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) CompanyMemberRole role,
            @RequestParam(required = false) CompanyMemberStatus status,
            @RequestParam(required = false) String search
    ) {
        GetCompanyMembersQuery query = new GetCompanyMembersQuery(
                companyId,
                page,
                size,
                role,
                status,
                search
        );

        GetCompanyMembersResult result = getCompanyMembersUseCase.handle(query);

        PagedResponse<CompanyMemberListItemResponse> response =
                companyMemberApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/company-members/{id}")
    public ResponseEntity<CompanyMemberResponse> getCompanyMemberById(
            @PathVariable UUID id
    ) {
        GetCompanyMemberByIdQuery query = new GetCompanyMemberByIdQuery(id);

        GetCompanyMemberByIdResult result = getCompanyMemberByIdUseCase.handle(query);

        CompanyMemberResponse response = companyMemberApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/company-members/{id}")
    public ResponseEntity<CompanyMemberResponse> updateCompanyMember(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCompanyMemberRequest request
    ) {
        UpdateCompanyMemberCommand command = companyMemberApiMapper.toCommand(id, request);

        UpdateCompanyMemberResult result = updateCompanyMemberUseCase.handle(command);

        CompanyMemberResponse response = companyMemberApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/company-members/{id}/deactivate")
    public ResponseEntity<CompanyMemberResponse> deactivateCompanyMember(
            @PathVariable UUID id
    ) {
        DeactivateCompanyMemberCommand command = new DeactivateCompanyMemberCommand(id);

        ChangeCompanyMemberStatusResult result = deactivateCompanyMemberUseCase.handle(command);

        CompanyMemberResponse response = companyMemberApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/company-members/{id}/activate")
    public ResponseEntity<CompanyMemberResponse> activateCompanyMember(
            @PathVariable UUID id
    ) {
        ActivateCompanyMemberCommand command = new ActivateCompanyMemberCommand(id);

        ChangeCompanyMemberStatusResult result = activateCompanyMemberUseCase.handle(command);

        CompanyMemberResponse response = companyMemberApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

}