package com.b2b.order.application.command.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateOrderItemCommand(
        UUID productId,
        String productName,
        BigDecimal quantity,
        BigDecimal unitPrice,
        String currency
) {
}