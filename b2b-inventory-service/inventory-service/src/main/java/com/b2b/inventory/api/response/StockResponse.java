package com.b2b.inventory.api.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record StockResponse(
        UUID id,
        UUID companyId,
        UUID productId,
        BigDecimal quantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}