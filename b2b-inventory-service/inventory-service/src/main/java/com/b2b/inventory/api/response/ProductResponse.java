package com.b2b.inventory.api.response;

import com.b2b.inventory.domain.enumtype.ProductStatus;
import com.b2b.inventory.domain.enumtype.ProductUnit;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        UUID companyId,
        String sku,
        String name,
        String description,
        ProductUnit unit,
        ProductStatus status,
        LocalDateTime createdAt
) {
}