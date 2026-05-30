package com.b2b.company.application.port.in;

import com.b2b.company.application.command.dto.UpdateCompanyCommand;
import com.b2b.company.application.command.dto.UpdateCompanyResult;

public interface UpdateCompanyUseCase {

    UpdateCompanyResult handle(UpdateCompanyCommand command);
}