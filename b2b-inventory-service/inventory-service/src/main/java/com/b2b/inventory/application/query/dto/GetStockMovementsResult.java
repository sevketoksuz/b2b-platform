package com.b2b.inventory.application.query.dto;

import java.util.List;

public record GetStockMovementsResult(
        List<StockMovementListItemResult> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}