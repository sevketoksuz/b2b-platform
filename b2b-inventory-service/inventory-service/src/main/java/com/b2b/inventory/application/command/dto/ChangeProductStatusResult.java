package com.b2b.inventory.application.command.dto;

import com.b2b.inventory.domain.enumtype.ProductStatus;
import com.b2b.inventory.domain.enumtype.ProductUnit;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChangeProductStatusResult(
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