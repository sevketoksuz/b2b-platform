package com.b2b.inventory.api.response;

import com.b2b.inventory.domain.enumtype.StockMovementType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record StockMovementListItemResponse(
        UUID id,
        UUID companyId,
        UUID productId,
        StockMovementType movementType,
        BigDecimal quantity,
        BigDecimal previousQuantity,
        BigDecimal newQuantity,
        String reason,
        LocalDateTime createdAt
) {
}