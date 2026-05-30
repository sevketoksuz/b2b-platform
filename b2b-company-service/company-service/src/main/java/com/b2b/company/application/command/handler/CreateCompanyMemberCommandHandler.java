package com.b2b.company.application.command.handler;

import com.b2b.company.application.command.dto.CreateCompanyMemberCommand;
import com.b2b.company.application.command.dto.CreateCompanyMemberResult;
import com.b2b.company.application.exception.CompanyMemberAlreadyExistsException;
import com.b2b.company.application.exception.CompanyNotActiveException;
import com.b2b.company.application.exception.CompanyNotFoundException;
import com.b2b.company.application.port.in.CreateCompanyMemberUseCase;
import com.b2b.company.application.port.out.CompanyMemberRepositoryPort;
import com.b2b.company.application.port.out.CompanyRepositoryPort;
import com.b2b.company.domain.model.Company;
import com.b2b.company.domain.model.CompanyMember;
import com.b2b.company.domain.valueobject.Email;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateCompanyMemberCommandHandler implements CreateCompanyMemberUseCase {

    private final CompanyRepositoryPort companyRepositoryPort;
    private final CompanyMemberRepositoryPort companyMemberRepositoryPort;

    public CreateCompanyMemberCommandHandler(
            CompanyRepositoryPort companyRepositoryPort,
            CompanyMemberRepositoryPort companyMemberRepositoryPort
    ) {
        this.companyRepositoryPort = companyRepositoryPort;
        this.companyMemberRepositoryPort = companyMemberRepositoryPort;
    }

    @Override
    @Transactional
    public CreateCompanyMemberResult handle(CreateCompanyMemberCommand command) {
        Company company = companyRepositoryPort.findById(command.companyId())
                .orElseThrow(() -> new CompanyNotFoundException(
                        "Company not found with id: " + command.companyId()
                ));

        if (!company.isActive()) {
            throw new CompanyNotActiveException(
                    "Cannot add member to inactive company: " + command.companyId()
            );
        }

        Email email = Email.of(command.email());

        if (companyMemberRepositoryPort.existsByCompanyIdAndEmail(command.companyId(), email)) {
            throw new CompanyMemberAlreadyExistsException(
                    "Company member already exists with email: " + email.getValue()
            );
        }

        CompanyMember companyMember = CompanyMember.create(
                command.companyId(),
                command.fullName(),
                email,
                command.role()
        );

        CompanyMember savedCompanyMember = companyMemberRepositoryPort.save(companyMember);

        return new CreateCompanyMemberResult(
                savedCompanyMember.getId(),
                savedCompanyMember.getCompanyId(),
                savedCompanyMember.getFullName(),
                savedCompanyMember.getEmail().getValue(),
                savedCompanyMember.getRole(),
                savedCompanyMember.getStatus(),
                savedCompanyMember.getCreatedAt()
        );
    }
}