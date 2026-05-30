package com.b2b.company.application.port.in;

import com.b2b.company.application.command.dto.ActivateCompanyMemberCommand;
import com.b2b.company.application.command.dto.ChangeCompanyMemberStatusResult;

public interface ActivateCompanyMemberUseCase {

    ChangeCompanyMemberStatusResult handle(ActivateCompanyMemberCommand command);
}