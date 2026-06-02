package com.b2b.order.application.event;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderConfirmedEventItem(
        UUID productId,
        String productName,
        BigDecimal quantity,
        BigDecimal unitPriceAmount,
        String currency,
        BigDecimal lineTotalAmount
) {
}