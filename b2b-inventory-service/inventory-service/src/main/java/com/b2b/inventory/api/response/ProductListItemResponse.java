package com.b2b.inventory.api.response;

import com.b2b.inventory.domain.enumtype.ProductStatus;
import com.b2b.inventory.domain.enumtype.ProductUnit;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductListItemResponse(
        UUID id,
        UUID companyId,
        String sku,
        String name,
        ProductUnit unit,
        ProductStatus status,
        LocalDateTime createdAt
) {
}