package com.b2b.order.application.command.dto;

import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
        UUID buyerCompanyId,
        UUID sellerCompanyId,
        List<CreateOrderItemCommand> items
) {
}