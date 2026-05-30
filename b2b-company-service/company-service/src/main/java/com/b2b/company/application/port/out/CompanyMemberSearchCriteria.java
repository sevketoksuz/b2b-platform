package com.b2b.company.application.port.out;

import com.b2b.company.domain.enumtype.CompanyMemberRole;
import com.b2b.company.domain.enumtype.CompanyMemberStatus;

import java.util.UUID;

public record CompanyMemberSearchCriteria(
        UUID companyId,
        int page,
        int size,
        CompanyMemberRole role,
        CompanyMemberStatus status,
        String search
) {
}