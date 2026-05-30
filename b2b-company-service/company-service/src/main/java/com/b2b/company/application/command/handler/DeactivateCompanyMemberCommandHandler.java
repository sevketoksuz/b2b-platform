package com.b2b.company.application.command.handler;

import com.b2b.company.application.command.dto.ChangeCompanyMemberStatusResult;
import com.b2b.company.application.command.dto.DeactivateCompanyMemberCommand;
import com.b2b.company.application.exception.CompanyMemberNotFoundException;
import com.b2b.company.application.port.in.DeactivateCompanyMemberUseCase;
import com.b2b.company.application.port.out.CompanyMemberRepositoryPort;
import com.b2b.company.domain.model.CompanyMember;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeactivateCompanyMemberCommandHandler implements DeactivateCompanyMemberUseCase {

    private final CompanyMemberRepositoryPort companyMemberRepositoryPort;

    public DeactivateCompanyMemberCommandHandler(
            CompanyMemberRepositoryPort companyMemberRepositoryPort
    ) {
        this.companyMemberRepositoryPort = companyMemberRepositoryPort;
    }

    @Override
    @Transactional
    public ChangeCompanyMemberStatusResult handle(DeactivateCompanyMemberCommand command) {
        CompanyMember member = companyMemberRepositoryPort.findById(command.id())
                .orElseThrow(() -> new CompanyMemberNotFoundException(
                        "Company member not found with id: " + command.id()
                ));

        member.deactivate();

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