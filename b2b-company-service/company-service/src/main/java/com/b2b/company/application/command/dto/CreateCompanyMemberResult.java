package com.b2b.company.application.command.dto;

import com.b2b.company.domain.enumtype.CompanyMemberRole;
import com.b2b.company.domain.enumtype.CompanyMemberStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateCompanyMemberResult(
        UUID id,
        UUID companyId,
        String fullName,
        String email,
        CompanyMemberRole role,
        CompanyMemberStatus status,
        LocalDateTime createdAt
) {
}