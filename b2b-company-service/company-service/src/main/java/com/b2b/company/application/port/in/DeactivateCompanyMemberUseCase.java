package com.b2b.company.application.port.in;

import com.b2b.company.application.command.dto.ChangeCompanyMemberStatusResult;
import com.b2b.company.application.command.dto.DeactivateCompanyMemberCommand;

public interface DeactivateCompanyMemberUseCase {

    ChangeCompanyMemberStatusResult handle(DeactivateCompanyMemberCommand command);
}