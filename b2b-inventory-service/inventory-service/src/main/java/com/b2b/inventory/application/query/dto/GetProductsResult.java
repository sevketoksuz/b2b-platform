package com.b2b.inventory.application.query.dto;

import java.util.List;

public record GetProductsResult(
        List<ProductListItemResult> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}