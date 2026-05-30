package com.b2b.company.application.query.dto;

import java.util.List;

public record GetCompanyMembersResult(
        List<CompanyMemberListItemResult> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}