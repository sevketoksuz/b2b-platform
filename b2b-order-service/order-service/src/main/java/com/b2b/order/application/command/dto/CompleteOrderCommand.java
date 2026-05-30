package com.b2b.order.application.command.dto;

import java.util.UUID;

public record CompleteOrderCommand(
        UUID orderId
) {
}