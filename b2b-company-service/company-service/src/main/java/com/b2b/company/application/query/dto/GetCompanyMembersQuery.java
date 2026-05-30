package com.b2b.company.application.query.dto;

import com.b2b.company.domain.enumtype.CompanyMemberRole;
import com.b2b.company.domain.enumtype.CompanyMemberStatus;

import java.util.UUID;

public record GetCompanyMembersQuery(
        UUID companyId,
        int page,
        int size,
        CompanyMemberRole role,
        CompanyMemberStatus status,
        String search
) {
}