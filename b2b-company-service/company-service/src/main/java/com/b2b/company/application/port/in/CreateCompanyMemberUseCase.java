package com.b2b.company.application.port.in;

import com.b2b.company.application.command.dto.CreateCompanyMemberCommand;
import com.b2b.company.application.command.dto.CreateCompanyMemberResult;

public interface CreateCompanyMemberUseCase {

    CreateCompanyMemberResult handle(CreateCompanyMemberCommand command);
}