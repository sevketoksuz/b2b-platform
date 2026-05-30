package com.b2b.order.api.response;

import com.b2b.order.domain.enumtype.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID buyerCompanyId,
        UUID sellerCompanyId,
        List<OrderItemResponse> items,
        OrderStatus status,
        BigDecimal totalAmount,
        String currency,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime confirmedAt,
        LocalDateTime cancelledAt,
        LocalDateTime completedAt
) {
}