package com.b2b.order.application.query.dto;

import com.b2b.order.domain.enumtype.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderListItemResult(
        UUID id,
        UUID buyerCompanyId,
        UUID sellerCompanyId,
        OrderStatus status,
        BigDecimal totalAmount,
        String currency,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}