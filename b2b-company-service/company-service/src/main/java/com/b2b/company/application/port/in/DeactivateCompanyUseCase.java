package com.b2b.company.application.port.in;

import com.b2b.company.application.command.dto.ChangeCompanyStatusResult;
import com.b2b.company.application.command.dto.DeactivateCompanyCommand;

public interface DeactivateCompanyUseCase {

    ChangeCompanyStatusResult handle(DeactivateCompanyCommand command);
}