package com.b2b.inventory.application.command.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AdjustStockCommand(
        UUID companyId,
        UUID productId,
        BigDecimal newQuantity,
        String reason
) {
}