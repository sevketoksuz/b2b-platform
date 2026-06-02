package com.b2b.inventory.infrastructure.messaging.rabbitmq.event;

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