package com.b2b.company.application.command.handler;

import com.b2b.company.application.command.dto.CreateCompanyCommand;
import com.b2b.company.application.command.dto.CreateCompanyResult;
import com.b2b.company.application.exception.CompanyAlreadyExistsException;
import com.b2b.company.application.port.out.CompanyRepositoryPort;
import com.b2b.company.domain.enumtype.CompanyStatus;
import com.b2b.company.domain.enumtype.CompanyType;
import com.b2b.company.domain.model.Company;
import com.b2b.company.domain.valueobject.TaxNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateCompanyCommandHandlerTest {

    private CompanyRepositoryPort companyRepositoryPort;
    private CreateCompanyCommandHandler handler;

    @BeforeEach
    void setUp() {
        companyRepositoryPort = mock(CompanyRepositoryPort.class);
        handler = new CreateCompanyCommandHandler(companyRepositoryPort);
    }

    @Test
    void shouldCreateCompanyWhenTaxNumberIsUnique() {
        CreateCompanyCommand command = new CreateCompanyCommand(
                "Acme Supplier Ltd",
                "1234567890",
                CompanyType.SUPPLIER
        );

        when(companyRepositoryPort.existsByTaxNumber(any(TaxNumber.class)))
                .thenReturn(false);

        when(companyRepositoryPort.save(any(Company.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateCompanyResult result = handler.handle(command);

        assertNotNull(result.id());
        assertEquals("Acme Supplier Ltd", result.name());
        assertEquals("1234567890", result.taxNumber());
        assertEquals(CompanyType.SUPPLIER, result.companyType());
        assertEquals(CompanyStatus.ACTIVE, result.status());
        assertNotNull(result.createdAt());

        verify(companyRepositoryPort).existsByTaxNumber(any(TaxNumber.class));
        verify(companyRepositoryPort).save(any(Company.class));
    }

    @Test
    void shouldThrowExceptionWhenTaxNumberAlreadyExists() {
        CreateCompanyCommand command = new CreateCompanyCommand(
                "Acme Supplier Ltd",
                "1234567890",
                CompanyType.SUPPLIER
        );

        when(companyRepositoryPort.existsByTaxNumber(any(TaxNumber.class)))
                .thenReturn(true);

        CompanyAlreadyExistsException exception = assertThrows(
                CompanyAlreadyExistsException.class,
                () -> handler.handle(command)
        );

        assertEquals(
                "Company already exists with tax number: 1234567890",
                exception.getMessage()
        );

        verify(companyRepositoryPort).existsByTaxNumber(any(TaxNumber.class));
        verify(companyRepositoryPort, never()).save(any(Company.class));
    }
}