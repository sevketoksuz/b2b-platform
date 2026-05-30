package com.b2b.order.application.query.dto;

import com.b2b.order.domain.enumtype.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetOrdersQuery(
        int page,
        int size,
        UUID buyerCompanyId,
        UUID sellerCompanyId,
        OrderStatus status,
        LocalDateTime fromDate,
        LocalDateTime toDate
) {
}