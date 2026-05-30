package com.b2b.inventory.application.command.dto;

import com.b2b.inventory.domain.enumtype.ProductUnit;

import java.util.UUID;

public record UpdateProductCommand(
        UUID id,
        String sku,
        String name,
        String description,
        ProductUnit unit
) {
}