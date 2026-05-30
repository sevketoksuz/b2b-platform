package com.b2b.company.application.port.out;

import com.b2b.company.domain.enumtype.CompanyStatus;
import com.b2b.company.domain.enumtype.CompanyType;

public record CompanySearchCriteria(
        int page,
        int size,
        CompanyType companyType,
        CompanyStatus status,
        String search
) {
}