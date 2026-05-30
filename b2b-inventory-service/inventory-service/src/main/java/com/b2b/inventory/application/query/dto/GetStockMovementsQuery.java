package com.b2b.inventory.application.query.dto;

import com.b2b.inventory.domain.enumtype.StockMovementType;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetStockMovementsQuery(
        int page,
        int size,
        UUID companyId,
        UUID productId,
        StockMovementType movementType,
        LocalDateTime fromDate,
        LocalDateTime toDate
) {
}