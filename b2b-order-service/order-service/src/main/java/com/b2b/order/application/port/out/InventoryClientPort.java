package com.b2b.order.application.port.out;

import java.math.BigDecimal;
import java.util.UUID;

public interface InventoryClientPort {

    void decreaseStock(
            UUID companyId,
            UUID productId,
            BigDecimal quantity,
            String reason
    );
}