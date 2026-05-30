package com.b2b.inventory.application.command.dto;

import com.b2b.inventory.domain.enumtype.ProductUnit;

import java.util.UUID;

public record CreateProductCommand(
        UUID companyId,
        String sku,
        String name,
        String description,
        ProductUnit unit
) {
}