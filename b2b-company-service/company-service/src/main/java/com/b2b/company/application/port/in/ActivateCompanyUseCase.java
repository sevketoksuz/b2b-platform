package com.b2b.company.application.port.in;

import com.b2b.company.application.command.dto.ActivateCompanyCommand;
import com.b2b.company.application.command.dto.ChangeCompanyStatusResult;

public interface ActivateCompanyUseCase {

    ChangeCompanyStatusResult handle(ActivateCompanyCommand command);
}