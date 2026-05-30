package com.b2b.company.application.command.dto;

import com.b2b.company.domain.enumtype.CompanyStatus;
import com.b2b.company.domain.enumtype.CompanyType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChangeCompanyStatusResult(
        UUID id,
        String name,
        String taxNumber,
        CompanyType companyType,
        CompanyStatus status,
        LocalDateTime createdAt
) {
}