package com.b2b.company.application.command.dto;

import com.b2b.company.domain.enumtype.CompanyType;

import java.util.UUID;

public record UpdateCompanyCommand(
        UUID id,
        String name,
        String taxNumber,
        CompanyType companyType
) {
}