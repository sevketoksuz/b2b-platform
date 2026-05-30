package com.b2b.order.infrastructure.client.inventory;

import java.math.BigDecimal;
import java.util.UUID;

public record DecreaseStockRequest(
        UUID companyId,
        BigDecimal quantity,
        String reason
) {
}