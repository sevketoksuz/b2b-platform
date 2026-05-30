package com.b2b.company.application.command.dto;

import java.util.UUID;

public record DeactivateCompanyCommand(
        UUID id
) {
}