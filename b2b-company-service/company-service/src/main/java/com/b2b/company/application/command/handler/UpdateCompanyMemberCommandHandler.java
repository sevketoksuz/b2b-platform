package com.b2b.company.application.command.handler;

import com.b2b.company.application.command.dto.UpdateCompanyMemberCommand;
import com.b2b.company.application.command.dto.UpdateCompanyMemberResult;
import com.b2b.company.application.exception.CompanyMemberAlreadyExistsException;
import com.b2b.company.application.exception.CompanyMemberNotFoundException;
import com.b2b.company.application.port.in.UpdateCompanyMemberUseCase;
import com.b2b.company.application.port.out.CompanyMemberRepositoryPort;
import com.b2b.company.domain.model.CompanyMember;
import com.b2b.company.domain.valueobject.Email;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateCompanyMemberCommandHandler implements UpdateCompanyMemberUseCase {

    private final CompanyMemberRepositoryPort companyMemberRepositoryPort;

    public UpdateCompanyMemberCommandHandler(
            CompanyMemberRepositoryPort companyMemberRepositoryPort
    ) {
        this.companyMemberRepositoryPort = companyMemberRepositoryPort;
    }

    @Override
    @Transactional
    public UpdateCompanyMemberResult handle(UpdateCompanyMemberCommand command) {
        CompanyMember member = companyMemberRepositoryPort.findById(command.id())
                .orElseThrow(() -> new CompanyMemberNotFoundException(
                        "Company member not found with id: " + command.id()
                ));

        Email email = Email.of(command.email());

        if (companyMemberRepositoryPort.existsByCompanyIdAndEmailAndIdNot(
                member.getCompanyId(),
                email,
                member.getId()
        )) {
            throw new CompanyMemberAlreadyExistsException(
                    "Another company member already exists with email: " + email.getValue()
            );
        }

        member.updateProfile(
                command.fullName(),
                email,
                command.role()
        );

        CompanyMember savedMember = companyMemberRepositoryPort.save(member);

        return new UpdateCompanyMemberResult(
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