package com.b2b.order.application.event;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCreatedEventItem(
        UUID productId,
        String productName,
        BigDecimal quantity,
        BigDecimal unitPriceAmount,
        String currency,
        BigDecimal lineTotalAmount
) {
}