package com.b2b.company.application.command.dto;

import com.b2b.company.domain.enumtype.CompanyType;

public record CreateCompanyCommand(
        String name,
        String taxNumber,
        CompanyType companyType
) {
}