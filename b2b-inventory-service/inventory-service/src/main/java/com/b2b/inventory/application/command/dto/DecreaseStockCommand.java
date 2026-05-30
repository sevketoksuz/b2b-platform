package com.b2b.inventory.application.command.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record DecreaseStockCommand(
        UUID companyId,
        UUID productId,
        BigDecimal quantity,
        String reason
) {
}