package com.b2b.inventory.application.query.dto;

import com.b2b.inventory.domain.enumtype.ProductStatus;
import com.b2b.inventory.domain.enumtype.ProductUnit;

import java.util.UUID;

public record GetProductsQuery(
        int page,
        int size,
        UUID companyId,
        ProductStatus status,
        ProductUnit unit,
        String search
) {
}