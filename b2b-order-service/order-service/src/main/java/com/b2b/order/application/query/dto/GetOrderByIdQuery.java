package com.b2b.order.application.query.dto;

import java.util.UUID;

public record GetOrderByIdQuery(
        UUID orderId
) {
}