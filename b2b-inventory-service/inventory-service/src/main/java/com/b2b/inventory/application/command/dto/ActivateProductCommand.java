package com.b2b.inventory.application.command.dto;

import java.util.UUID;

public record ActivateProductCommand(
        UUID id
) {
}