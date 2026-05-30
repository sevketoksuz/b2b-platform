package com.b2b.order.api.response;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        UUID productId,
        String productName,
        BigDecimal quantity,
        BigDecimal unitPriceAmount,
        String unitPriceCurrency,
        BigDecimal lineTotalAmount,
        String lineTotalCurrency
) {
}