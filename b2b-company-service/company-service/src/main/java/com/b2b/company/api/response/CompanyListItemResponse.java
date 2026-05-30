package com.b2b.company.api.response;

import com.b2b.company.domain.enumtype.CompanyStatus;
import com.b2b.company.domain.enumtype.CompanyType;

import java.time.LocalDateTime;
import java.util.UUID;

public record CompanyListItemResponse(
        UUID id,
        String name,
        String taxNumber,
        CompanyType companyType,
        CompanyStatus status,
        LocalDateTime createdAt
) {
}