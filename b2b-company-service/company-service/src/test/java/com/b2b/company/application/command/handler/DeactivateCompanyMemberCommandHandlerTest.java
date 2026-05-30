package com.b2b.company.application.command.handler;

import com.b2b.company.application.command.dto.ChangeCompanyMemberStatusResult;
import com.b2b.company.application.command.dto.DeactivateCompanyMemberCommand;
import com.b2b.company.application.exception.CompanyMemberNotFoundException;
import com.b2b.company.application.port.out.CompanyMemberRepositoryPort;
import com.b2b.company.domain.enumtype.CompanyMemberRole;
import com.b2b.company.domain.enumtype.CompanyMemberStatus;
import com.b2b.company.domain.exception.CompanyMemberDomainException;
import com.b2b.company.domain.model.CompanyMember;
import com.b2b.company.domain.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeactivateCompanyMemberCommandHandlerTest {

    private CompanyMemberRepositoryPort companyMemberRepositoryPort;
    private DeactivateCompanyMemberCommandHandler handler;

    @BeforeEach
    void setUp() {
        companyMemberRepositoryPort = mock(CompanyMemberRepositoryPort.class);
        handler = new DeactivateCompanyMemberCommandHandler(companyMemberRepositoryPort);
    }

    @Test
    void shouldDeactivateCompanyMemberWhenMemberExistsAndIsActive() {
        UUID memberId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        CompanyMember member = CompanyMember.restore(
                memberId,
                companyId,
                "John Doe",
                Email.of("john@acme.com"),
                CompanyMemberRole.ADMIN,
                CompanyMemberStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );

        DeactivateCompanyMemberCommand command = new DeactivateCompanyMemberCommand(memberId);

        when(companyMemberRepositoryPort.findById(memberId))
                .thenReturn(Optional.of(member));

        when(companyMemberRepositoryPort.save(any(CompanyMember.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ChangeCompanyMemberStatusResult result = handler.handle(command);

        assertEquals(memberId, result.id());
        assertEquals(companyId, result.companyId());
        assertEquals("John Doe", result.fullName());
        assertEquals("john@acme.com", result.email());
        assertEquals(CompanyMemberRole.ADMIN, result.role());
        assertEquals(CompanyMemberStatus.INACTIVE, result.status());

        verify(companyMemberRepositoryPort).findById(memberId);
        verify(companyMemberRepositoryPort).save(any(CompanyMember.class));
    }

    @Test
    void shouldThrowExceptionWhenCompanyMemberNotFound() {
        UUID memberId = UUID.randomUUID();

        DeactivateCompanyMemberCommand command = new DeactivateCompanyMemberCommand(memberId);

        when(companyMemberRepositoryPort.findById(memberId))
                .thenReturn(Optional.empty());

        CompanyMemberNotFoundException exception = assertThrows(
                CompanyMemberNotFoundException.class,
                () -> handler.handle(command)
        );

        assertEquals("Company member not found with id: " + memberId, exception.getMessage());

        verify(companyMemberRepositoryPort).findById(memberId);
        verify(companyMemberRepositoryPort, never()).save(any(CompanyMember.class));
    }

    @Test
    void shouldThrowExceptionWhenCompanyMemberAlreadyInactive() {
        UUID memberId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        CompanyMember member = CompanyMember.restore(
                memberId,
                companyId,
                "John Doe",
                Email.of("john@acme.com"),
                CompanyMemberRole.ADMIN,
                CompanyMemberStatus.INACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );

        DeactivateCompanyMemberCommand command = new DeactivateCompanyMemberCommand(memberId);

        when(companyMemberRepositoryPort.findById(memberId))
                .thenReturn(Optional.of(member));

        CompanyMemberDomainException exception = assertThrows(
                CompanyMemberDomainException.class,
                () -> handler.handle(command)
        );

        assertEquals("Company member is already inactive.", exception.getMessage());

        verify(companyMemberRepositoryPort).findById(memberId);
        verify(companyMemberRepositoryPort, never()).save(any(CompanyMember.class));
    }
}