package com.b2b.company.api.response;

import com.b2b.company.domain.enumtype.CompanyMemberRole;
import com.b2b.company.domain.enumtype.CompanyMemberStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record CompanyMemberListItemResponse(
        UUID id,
        UUID companyId,
        String fullName,
        String email,
        CompanyMemberRole role,
        CompanyMemberStatus status,
        LocalDateTime createdAt
) {
}