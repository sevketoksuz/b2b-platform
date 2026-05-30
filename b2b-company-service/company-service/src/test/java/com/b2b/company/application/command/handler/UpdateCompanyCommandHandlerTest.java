package com.b2b.company.application.command.handler;

import com.b2b.company.application.command.dto.UpdateCompanyCommand;
import com.b2b.company.application.command.dto.UpdateCompanyResult;
import com.b2b.company.application.exception.CompanyAlreadyExistsException;
import com.b2b.company.application.exception.CompanyNotFoundException;
import com.b2b.company.application.port.out.CompanyRepositoryPort;
import com.b2b.company.domain.enumtype.CompanyStatus;
import com.b2b.company.domain.enumtype.CompanyType;
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

class UpdateCompanyCommandHandlerTest {

    private CompanyRepositoryPort companyRepositoryPort;
    private UpdateCompanyCommandHandler handler;

    @BeforeEach
    void setUp() {
        companyRepositoryPort = mock(CompanyRepositoryPort.class);
        handler = new UpdateCompanyCommandHandler(companyRepositoryPort);
    }

    @Test
    void shouldUpdateCompanyWhenCompanyExistsAndTaxNumberIsUnique() {
        UUID companyId = UUID.randomUUID();

        Company existingCompany = Company.restore(
                companyId,
                "Acme Supplier Ltd",
                TaxNumber.of("1234567890"),
                CompanyType.SUPPLIER,
                CompanyStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );

        UpdateCompanyCommand command = new UpdateCompanyCommand(
                companyId,
                "Acme Updated Ltd",
                "9876543210",
                CompanyType.BOTH
        );

        when(companyRepositoryPort.findById(companyId))
                .thenReturn(Optional.of(existingCompany));

        when(companyRepositoryPort.existsByTaxNumberAndIdNot(any(TaxNumber.class), eq(companyId)))
                .thenReturn(false);

        when(companyRepositoryPort.save(any(Company.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UpdateCompanyResult result = handler.handle(command);

        assertEquals(companyId, result.id());
        assertEquals("Acme Updated Ltd", result.name());
        assertEquals("9876543210", result.taxNumber());
        assertEquals(CompanyType.BOTH, result.companyType());
        assertEquals(CompanyStatus.ACTIVE, result.status());

        verify(companyRepositoryPort).findById(companyId);
        verify(companyRepositoryPort).existsByTaxNumberAndIdNot(any(TaxNumber.class), eq(companyId));
        verify(companyRepositoryPort).save(any(Company.class));
    }

    @Test
    void shouldThrowExceptionWhenCompanyNotFound() {
        UUID companyId = UUID.randomUUID();

        UpdateCompanyCommand command = new UpdateCompanyCommand(
                companyId,
                "Acme Updated Ltd",
                "9876543210",
                CompanyType.BOTH
        );

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
    void shouldThrowExceptionWhenAnotherCompanyHasSameTaxNumber() {
        UUID companyId = UUID.randomUUID();

        Company existingCompany = Company.restore(
                companyId,
                "Acme Supplier Ltd",
                TaxNumber.of("1234567890"),
                CompanyType.SUPPLIER,
                CompanyStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );

        UpdateCompanyCommand command = new UpdateCompanyCommand(
                companyId,
                "Acme Updated Ltd",
                "9876543210",
                CompanyType.BOTH
        );

        when(companyRepositoryPort.findById(companyId))
                .thenReturn(Optional.of(existingCompany));

        when(companyRepositoryPort.existsByTaxNumberAndIdNot(any(TaxNumber.class), eq(companyId)))
                .thenReturn(true);

        CompanyAlreadyExistsException exception = assertThrows(
                CompanyAlreadyExistsException.class,
                () -> handler.handle(command)
        );

        assertEquals(
                "Another company already exists with tax number: 9876543210",
                exception.getMessage()
        );

        verify(companyRepositoryPort).findById(companyId);
        verify(companyRepositoryPort).existsByTaxNumberAndIdNot(any(TaxNumber.class), eq(companyId));
        verify(companyRepositoryPort, never()).save(any(Company.class));
    }
}