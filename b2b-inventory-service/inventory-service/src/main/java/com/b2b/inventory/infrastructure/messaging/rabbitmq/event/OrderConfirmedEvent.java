package com.b2b.inventory.infrastructure.messaging.rabbitmq.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderConfirmedEvent(
        UUID orderId,
        UUID buyerCompanyId,
        UUID sellerCompanyId,
        List<OrderConfirmedEventItem> items,
        LocalDateTime createdAt
) {
}