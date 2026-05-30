package com.b2b.company.application.command.dto;

import com.b2b.company.domain.enumtype.CompanyMemberRole;

import java.util.UUID;

public record CreateCompanyMemberCommand(
        UUID companyId,
        String fullName,
        String email,
        CompanyMemberRole role
) {
}