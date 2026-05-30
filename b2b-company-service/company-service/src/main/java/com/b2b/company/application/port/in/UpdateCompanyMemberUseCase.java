package com.b2b.company.application.port.in;

import com.b2b.company.application.command.dto.UpdateCompanyMemberCommand;
import com.b2b.company.application.command.dto.UpdateCompanyMemberResult;

public interface UpdateCompanyMemberUseCase {

    UpdateCompanyMemberResult handle(UpdateCompanyMemberCommand command);
}