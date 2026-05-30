package com.b2b.company.application.query.dto;

import java.util.List;

public record GetCompaniesResult(
        List<CompanyListItemResult> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}