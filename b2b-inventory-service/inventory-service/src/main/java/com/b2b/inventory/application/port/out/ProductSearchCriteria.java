package com.b2b.inventory.application.port.out;

import com.b2b.inventory.domain.enumtype.ProductStatus;
import com.b2b.inventory.domain.enumtype.ProductUnit;

import java.util.UUID;

public record ProductSearchCriteria(
        int page,
        int size,
        UUID companyId,
        ProductStatus status,
        ProductUnit unit,
        String search
) {
}