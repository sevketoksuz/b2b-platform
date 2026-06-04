package com.b2b.inventory.application.command.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProcessOrderConfirmedItemCommand(
        UUID orderId,
        UUID sellerCompanyId,
        UUID productId,
        BigDecimal quantity
) {
}