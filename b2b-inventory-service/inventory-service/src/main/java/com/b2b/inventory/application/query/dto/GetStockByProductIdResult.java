package com.b2b.inventory.application.query.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record GetStockByProductIdResult(
        UUID id,
        UUID companyId,
        UUID productId,
        BigDecimal quantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}