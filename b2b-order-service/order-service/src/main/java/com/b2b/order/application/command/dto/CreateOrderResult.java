package com.b2b.order.application.command.dto;

import com.b2b.order.domain.enumtype.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateOrderResult(
        UUID id,
        UUID buyerCompanyId,
        UUID sellerCompanyId,
        List<OrderItemResult> items,
        OrderStatus status,
        BigDecimal totalAmount,
        String currency,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}