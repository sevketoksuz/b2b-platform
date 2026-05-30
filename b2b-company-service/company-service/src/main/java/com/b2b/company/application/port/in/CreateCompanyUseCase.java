package com.b2b.company.application.port.in;

import com.b2b.company.application.command.dto.CreateCompanyCommand;
import com.b2b.company.application.command.dto.CreateCompanyResult;

public interface CreateCompanyUseCase {

    CreateCompanyResult handle(CreateCompanyCommand command);
}