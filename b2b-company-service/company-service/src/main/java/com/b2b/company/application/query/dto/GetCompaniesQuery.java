package com.b2b.company.application.query.dto;

import com.b2b.company.domain.enumtype.CompanyStatus;
import com.b2b.company.domain.enumtype.CompanyType;

public record GetCompaniesQuery(
        int page,
        int size,
        CompanyType companyType,
        CompanyStatus status,
        String search
) {
}