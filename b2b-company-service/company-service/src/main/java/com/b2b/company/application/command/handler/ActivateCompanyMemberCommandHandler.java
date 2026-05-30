package com.b2b.company.application.command.handler;

import com.b2b.company.application.command.dto.ActivateCompanyMemberCommand;
import com.b2b.company.application.command.dto.ChangeCompanyMemberStatusResult;
import com.b2b.company.application.exception.CompanyMemberNotFoundException;
import com.b2b.company.application.port.in.ActivateCompanyMemberUseCase;
import com.b2b.company.application.port.out.CompanyMemberRepositoryPort;
import com.b2b.company.domain.model.CompanyMember;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivateCompanyMemberCommandHandler implements ActivateCompanyMemberUseCase {

    private final CompanyMemberRepositoryPort companyMemberRepositoryPort;

    public ActivateCompanyMemberCommandHandler(
            CompanyMemberRepositoryPort companyMemberRepositoryPort
    ) {
        this.companyMemberRepositoryPort = companyMemberRepositoryPort;
    }

    @Override
    @Transactional
    public ChangeCompanyMemberStatusResult handle(ActivateCompanyMemberCommand command) {
        CompanyMember member = companyMemberRepositoryPort.findById(command.id())
                .orElseThrow(() -> new CompanyMemberNotFoundException(
                        "Company member not found with id: " + command.id()
                ));

        member.activate();

        CompanyMember savedMember = companyMemberRepositoryPort.save(member);

        return new ChangeCompanyMemberStatusResult(
                savedMember.getId(),
                savedMember.getCompanyId(),
                savedMember.getFullName(),
                savedMember.getEmail().getValue(),
                savedMember.getRole(),
                savedMember.getStatus(),
                savedMember.getCreatedAt()
        );
    }
}