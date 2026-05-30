package com.b2b.company.application.command.dto;

import java.util.UUID;

public record DeactivateCompanyMemberCommand(
        UUID id
) {
}