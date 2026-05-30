package com.b2b.company.application.command.handler;

import com.b2b.company.application.command.dto.ActivateCompanyCommand;
import com.b2b.company.application.command.dto.ChangeCompanyStatusResult;
import com.b2b.company.application.exception.CompanyNotFoundException;
import com.b2b.company.application.port.out.CompanyRepositoryPort;
import com.b2b.company.domain.enumtype.CompanyStatus;
import com.b2b.company.domain.enumtype.CompanyType;
import com.b2b.company.domain.exception.CompanyDomainException;
import com.b2b.company.domain.model.Company;
import com.b2b.company.domain.valueobject.TaxNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ActivateCompanyCommandHandlerTest {

    private CompanyRepositoryPort companyRepositoryPort;
    private ActivateCompanyCommandHandler handler;

    @BeforeEach
    void setUp() {
        companyRepositoryPort = mock(CompanyRepositoryPort.class);
        handler = new ActivateCompanyCommandHandler(companyRepositoryPort);
    }

    @Test
    void shouldActivateCompanyWhenCompanyExistsAndIsInactive() {
        UUID companyId = UUID.randomUUID();

        Company company = Company.restore(
                companyId,
                "Acme Supplier Ltd",
                TaxNumber.of("1234567890"),
                CompanyType.SUPPLIER,
                CompanyStatus.INACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );

        ActivateCompanyCommand command = new ActivateCompanyCommand(companyId);

        when(companyRepositoryPort.findById(companyId))
                .thenReturn(Optional.of(company));

        when(companyRepositoryPort.save(any(Company.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ChangeCompanyStatusResult result = handler.handle(command);

        assertEquals(companyId, result.id());
        assertEquals("Acme Supplier Ltd", result.name());
        assertEquals("1234567890", result.taxNumber());
        assertEquals(CompanyType.SUPPLIER, result.companyType());
        assertEquals(CompanyStatus.ACTIVE, result.status());

        verify(companyRepositoryPort).findById(companyId);
        verify(companyRepositoryPort).save(any(Company.class));
    }

    @Test
    void shouldThrowExceptionWhenCompanyNotFound() {
        UUID companyId = UUID.randomUUID();

        ActivateCompanyCommand command = new ActivateCompanyCommand(companyId);

        when(companyRepositoryPort.findById(companyId))
                .thenReturn(Optional.empty());

        CompanyNotFoundException exception = assertThrows(
                CompanyNotFoundException.class,
                () -> handler.handle(command)
        );

        assertEquals("Company not found with id: " + companyId, exception.getMessage());

        verify(companyRepositoryPort).findById(companyId);
        verify(companyRepositoryPort, never()).save(any(Company.class));
    }

    @Test
    void shouldThrowExceptionWhenCompanyAlreadyActive() {
        UUID companyId = UUID.randomUUID();

        Company company = Company.restore(
                companyId,
                "Acme Supplier Ltd",
                TaxNumber.of("1234567890"),
                CompanyType.SUPPLIER,
                CompanyStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );

        ActivateCompanyCommand command = new ActivateCompanyCommand(companyId);

        when(companyRepositoryPort.findById(companyId))
                .thenReturn(Optional.of(company));

        CompanyDomainException exception = assertThrows(
                CompanyDomainException.class,
                () -> handler.handle(command)
        );

        assertEquals("Company is already active.", exception.getMessage());

        verify(companyRepositoryPort).findById(companyId);
        verify(companyRepositoryPort, never()).save(any(Company.class));
    }
}