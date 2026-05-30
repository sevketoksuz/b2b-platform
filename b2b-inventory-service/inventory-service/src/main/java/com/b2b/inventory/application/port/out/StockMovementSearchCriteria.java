package com.b2b.inventory.application.port.out;

import com.b2b.inventory.domain.enumtype.StockMovementType;

import java.time.LocalDateTime;
import java.util.UUID;

public record StockMovementSearchCriteria(
        int page,
        int size,
        UUID companyId,
        UUID productId,
        StockMovementType movementType,
        LocalDateTime fromDate,
        LocalDateTime toDate
) {
}