package com.b2b.company.application.command.handler;

import com.b2b.company.application.command.dto.CreateCompanyMemberCommand;
import com.b2b.company.application.command.dto.CreateCompanyMemberResult;
import com.b2b.company.application.exception.CompanyMemberAlreadyExistsException;
import com.b2b.company.application.exception.CompanyNotActiveException;
import com.b2b.company.application.exception.CompanyNotFoundException;
import com.b2b.company.application.port.out.CompanyMemberRepositoryPort;
import com.b2b.company.application.port.out.CompanyRepositoryPort;
import com.b2b.company.domain.enumtype.CompanyMemberRole;
import com.b2b.company.domain.enumtype.CompanyMemberStatus;
import com.b2b.company.domain.enumtype.CompanyStatus;
import com.b2b.company.domain.enumtype.CompanyType;
import com.b2b.company.domain.model.Company;
import com.b2b.company.domain.model.CompanyMember;
import com.b2b.company.domain.valueobject.Email;
import com.b2b.company.domain.valueobject.TaxNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateCompanyMemberCommandHandlerTest {

    private CompanyRepositoryPort companyRepositoryPort;
    private CompanyMemberRepositoryPort companyMemberRepositoryPort;
    private CreateCompanyMemberCommandHandler handler;

    @BeforeEach
    void setUp() {
        companyRepositoryPort = mock(CompanyRepositoryPort.class);
        companyMemberRepositoryPort = mock(CompanyMemberRepositoryPort.class);

        handler = new CreateCompanyMemberCommandHandler(
                companyRepositoryPort,
                companyMemberRepositoryPort
        );
    }

    @Test
    void shouldCreateCompanyMemberWhenCompanyIsActiveAndEmailIsUnique() {
        UUID companyId = UUID.randomUUID();

        Company company = activeCompany(companyId);

        CreateCompanyMemberCommand command = new CreateCompanyMemberCommand(
                companyId,
                "John Doe",
                "john@acme.com",
                CompanyMemberRole.ADMIN
        );

        when(companyRepositoryPort.findById(companyId))
                .thenReturn(Optional.of(company));

        when(companyMemberRepositoryPort.existsByCompanyIdAndEmail(eq(companyId), any(Email.class)))
                .thenReturn(false);

        when(companyMemberRepositoryPort.save(any(CompanyMember.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateCompanyMemberResult result = handler.handle(command);

        assertNotNull(result.id());
        assertEquals(companyId, result.companyId());
        assertEquals("John Doe", result.fullName());
        assertEquals("john@acme.com", result.email());
        assertEquals(CompanyMemberRole.ADMIN, result.role());
        assertEquals(CompanyMemberStatus.ACTIVE, result.status());

        verify(companyRepositoryPort).findById(companyId);
        verify(companyMemberRepositoryPort).existsByCompanyIdAndEmail(eq(companyId), any(Email.class));
        verify(companyMemberRepositoryPort).save(any(CompanyMember.class));
    }

    @Test
    void shouldThrowExceptionWhenCompanyNotFound() {
        UUID companyId = UUID.randomUUID();

        CreateCompanyMemberCommand command = new CreateCompanyMemberCommand(
                companyId,
                "John Doe",
                "john@acme.com",
                CompanyMemberRole.ADMIN
        );

        when(companyRepositoryPort.findById(companyId))
                .thenReturn(Optional.empty());

        CompanyNotFoundException exception = assertThrows(
                CompanyNotFoundException.class,
                () -> handler.handle(command)
        );

        assertEquals("Company not found with id: " + companyId, exception.getMessage());

        verify(companyRepositoryPort).findById(companyId);
        verify(companyMemberRepositoryPort, never()).save(any(CompanyMember.class));
    }

    @Test
    void shouldThrowExceptionWhenCompanyIsInactive() {
        UUID companyId = UUID.randomUUID();

        Company inactiveCompany = inactiveCompany(companyId);

        CreateCompanyMemberCommand command = new CreateCompanyMemberCommand(
                companyId,
                "John Doe",
                "john@acme.com",
                CompanyMemberRole.ADMIN
        );

        when(companyRepositoryPort.findById(companyId))
                .thenReturn(Optional.of(inactiveCompany));

        CompanyNotActiveException exception = assertThrows(
                CompanyNotActiveException.class,
                () -> handler.handle(command)
        );

        assertEquals(
                "Cannot add member to inactive company: " + companyId,
                exception.getMessage()
        );

        verify(companyRepositoryPort).findById(companyId);
        verify(companyMemberRepositoryPort, never()).save(any(CompanyMember.class));
    }

    @Test
    void shouldThrowExceptionWhenMemberEmailAlreadyExistsInSameCompany() {
        UUID companyId = UUID.randomUUID();

        Company company = activeCompany(companyId);

        CreateCompanyMemberCommand command = new CreateCompanyMemberCommand(
                companyId,
                "John Doe",
                "john@acme.com",
                CompanyMemberRole.ADMIN
        );

        when(companyRepositoryPort.findById(companyId))
                .thenReturn(Optional.of(company));

        when(companyMemberRepositoryPort.existsByCompanyIdAndEmail(eq(companyId), any(Email.class)))
                .thenReturn(true);

        CompanyMemberAlreadyExistsException exception = assertThrows(
                CompanyMemberAlreadyExistsException.class,
                () -> handler.handle(command)
        );

        assertEquals(
                "Company member already exists with email: john@acme.com",
                exception.getMessage()
        );

        verify(companyRepositoryPort).findById(companyId);
        verify(companyMemberRepositoryPort).existsByCompanyIdAndEmail(eq(companyId), any(Email.class));
        verify(companyMemberRepositoryPort, never()).save(any(CompanyMember.class));
    }

    private Company activeCompany(UUID companyId) {
        return Company.restore(
                companyId,
                "Acme Supplier Ltd",
                TaxNumber.of("1234567890"),
                CompanyType.SUPPLIER,
                CompanyStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );
    }

    private Company inactiveCompany(UUID companyId) {
        return Company.restore(
                companyId,
                "Acme Supplier Ltd",
                TaxNumber.of("1234567890"),
                CompanyType.SUPPLIER,
                CompanyStatus.INACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );
    }
}