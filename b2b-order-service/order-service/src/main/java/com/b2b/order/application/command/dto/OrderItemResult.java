package com.b2b.order.application.command.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResult(
        UUID id,
        UUID productId,
        String productName,
        BigDecimal quantity,
        BigDecimal unitPriceAmount,
        String unitPriceCurrency,
        BigDecimal lineTotalAmount,
        String lineTotalCurrency
) {
}