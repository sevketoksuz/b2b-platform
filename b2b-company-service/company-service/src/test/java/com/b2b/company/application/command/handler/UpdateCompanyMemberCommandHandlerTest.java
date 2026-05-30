package com.b2b.company.application.command.handler;

import com.b2b.company.application.command.dto.UpdateCompanyMemberCommand;
import com.b2b.company.application.command.dto.UpdateCompanyMemberResult;
import com.b2b.company.application.exception.CompanyMemberAlreadyExistsException;
import com.b2b.company.application.exception.CompanyMemberNotFoundException;
import com.b2b.company.application.port.out.CompanyMemberRepositoryPort;
import com.b2b.company.domain.enumtype.CompanyMemberRole;
import com.b2b.company.domain.enumtype.CompanyMemberStatus;
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

class UpdateCompanyMemberCommandHandlerTest {

    private CompanyMemberRepositoryPort companyMemberRepositoryPort;
    private UpdateCompanyMemberCommandHandler handler;

    @BeforeEach
    void setUp() {
        companyMemberRepositoryPort = mock(CompanyMemberRepositoryPort.class);
        handler = new UpdateCompanyMemberCommandHandler(companyMemberRepositoryPort);
    }

    @Test
    void shouldUpdateCompanyMemberWhenMemberExistsAndEmailIsUnique() {
        UUID memberId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        CompanyMember existingMember = CompanyMember.restore(
                memberId,
                companyId,
                "John Doe",
                Email.of("john@acme.com"),
                CompanyMemberRole.ADMIN,
                CompanyMemberStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );

        UpdateCompanyMemberCommand command = new UpdateCompanyMemberCommand(
                memberId,
                "John Updated",
                "john.updated@acme.com",
                CompanyMemberRole.EMPLOYEE
        );

        when(companyMemberRepositoryPort.findById(memberId))
                .thenReturn(Optional.of(existingMember));

        when(companyMemberRepositoryPort.existsByCompanyIdAndEmailAndIdNot(
                eq(companyId),
                any(Email.class),
                eq(memberId)
        )).thenReturn(false);

        when(companyMemberRepositoryPort.save(any(CompanyMember.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UpdateCompanyMemberResult result = handler.handle(command);

        assertEquals(memberId, result.id());
        assertEquals(companyId, result.companyId());
        assertEquals("John Updated", result.fullName());
        assertEquals("john.updated@acme.com", result.email());
        assertEquals(CompanyMemberRole.EMPLOYEE, result.role());
        assertEquals(CompanyMemberStatus.ACTIVE, result.status());

        verify(companyMemberRepositoryPort).findById(memberId);
        verify(companyMemberRepositoryPort).existsByCompanyIdAndEmailAndIdNot(
                eq(companyId),
                any(Email.class),
                eq(memberId)
        );
        verify(companyMemberRepositoryPort).save(any(CompanyMember.class));
    }

    @Test
    void shouldThrowExceptionWhenCompanyMemberNotFound() {
        UUID memberId = UUID.randomUUID();

        UpdateCompanyMemberCommand command = new UpdateCompanyMemberCommand(
                memberId,
                "John Updated",
                "john.updated@acme.com",
                CompanyMemberRole.EMPLOYEE
        );

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
    void shouldThrowExceptionWhenAnotherMemberHasSameEmailInSameCompany() {
        UUID memberId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        CompanyMember existingMember = CompanyMember.restore(
                memberId,
                companyId,
                "John Doe",
                Email.of("john@acme.com"),
                CompanyMemberRole.ADMIN,
                CompanyMemberStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );

        UpdateCompanyMemberCommand command = new UpdateCompanyMemberCommand(
                memberId,
                "John Updated",
                "jane@acme.com",
                CompanyMemberRole.EMPLOYEE
        );

        when(companyMemberRepositoryPort.findById(memberId))
                .thenReturn(Optional.of(existingMember));

        when(companyMemberRepositoryPort.existsByCompanyIdAndEmailAndIdNot(
                eq(companyId),
                any(Email.class),
                eq(memberId)
        )).thenReturn(true);

        CompanyMemberAlreadyExistsException exception = assertThrows(
                CompanyMemberAlreadyExistsException.class,
                () -> handler.handle(command)
        );

        assertEquals(
                "Another company member already exists with email: jane@acme.com",
                exception.getMessage()
        );

        verify(companyMemberRepositoryPort).findById(memberId);
        verify(companyMemberRepositoryPort).existsByCompanyIdAndEmailAndIdNot(
                eq(companyId),
                any(Email.class),
                eq(memberId)
        );
        verify(companyMemberRepositoryPort, never()).save(any(CompanyMember.class));
    }
}