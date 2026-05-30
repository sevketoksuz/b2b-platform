package com.b2b.order.application.query.dto;

import java.util.List;

public record GetOrdersResult(
        List<OrderListItemResult> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}