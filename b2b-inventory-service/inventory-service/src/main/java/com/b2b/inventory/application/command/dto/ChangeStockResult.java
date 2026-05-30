package com.b2b.inventory.application.command.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ChangeStockResult(
        UUID id,
        UUID companyId,
        UUID productId,
        BigDecimal quantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}