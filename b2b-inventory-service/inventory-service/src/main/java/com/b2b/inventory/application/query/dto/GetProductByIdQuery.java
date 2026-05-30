package com.b2b.inventory.application.query.dto;

import java.util.UUID;

public record GetProductByIdQuery(
        UUID id
) {
}