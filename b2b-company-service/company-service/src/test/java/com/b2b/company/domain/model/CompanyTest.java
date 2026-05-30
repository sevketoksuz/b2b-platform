package com.b2b.company.domain.model;

import com.b2b.company.domain.enumtype.CompanyStatus;
import com.b2b.company.domain.enumtype.CompanyType;
import com.b2b.company.domain.exception.CompanyDomainException;
import com.b2b.company.domain.valueobject.TaxNumber;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompanyTest {

    @Test
    void shouldCreateCompanyAsActive() {
        Company company = Company.create(
                "Acme Supplier Ltd",
                TaxNumber.of("1234567890"),
                CompanyType.SUPPLIER
        );

        assertNotNull(company.getId());
        assertEquals("Acme Supplier Ltd", company.getName());
        assertEquals("1234567890", company.getTaxNumber().getValue());
        assertEquals(CompanyType.SUPPLIER, company.getCompanyType());
        assertEquals(CompanyStatus.ACTIVE, company.getStatus());
        assertTrue(company.isActive());
        assertNotNull(company.getCreatedAt());
        assertNotNull(company.getUpdatedAt());
    }

    @Test
    void shouldUpdateCompanyDetails() {
        Company company = Company.create(
                "Acme Supplier Ltd",
                TaxNumber.of("1234567890"),
                CompanyType.SUPPLIER
        );

        company.updateDetails(
                "Acme Updated Ltd",
                TaxNumber.of("9876543210"),
                CompanyType.BOTH
        );

        assertEquals("Acme Updated Ltd", company.getName());
        assertEquals("9876543210", company.getTaxNumber().getValue());
        assertEquals(CompanyType.BOTH, company.getCompanyType());
    }

    @Test
    void shouldDeactivateCompany() {
        Company company = Company.create(
                "Acme Supplier Ltd",
                TaxNumber.of("1234567890"),
                CompanyType.SUPPLIER
        );

        company.deactivate();

        assertEquals(CompanyStatus.INACTIVE, company.getStatus());
        assertFalse(company.isActive());
    }

    @Test
    void shouldActivateInactiveCompany() {
        Company company = Company.create(
                "Acme Supplier Ltd",
                TaxNumber.of("1234567890"),
                CompanyType.SUPPLIER
        );

        company.deactivate();
        company.activate();

        assertEquals(CompanyStatus.ACTIVE, company.getStatus());
        assertTrue(company.isActive());
    }

    @Test
    void shouldThrowExceptionWhenCompanyAlreadyInactive() {
        Company company = Company.create(
                "Acme Supplier Ltd",
                TaxNumber.of("1234567890"),
                CompanyType.SUPPLIER
        );

        company.deactivate();

        CompanyDomainException exception = assertThrows(
                CompanyDomainException.class,
                company::deactivate
        );

        assertEquals("Company is already inactive.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCompanyAlreadyActive() {
        Company company = Company.create(
                "Acme Supplier Ltd",
                TaxNumber.of("1234567890"),
                CompanyType.SUPPLIER
        );

        CompanyDomainException exception = assertThrows(
                CompanyDomainException.class,
                company::activate
        );

        assertEquals("Company is already active.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCompanyNameIsBlank() {
        CompanyDomainException exception = assertThrows(
                CompanyDomainException.class,
                () -> Company.create(
                        "",
                        TaxNumber.of("1234567890"),
                        CompanyType.SUPPLIER
                )
        );

        assertEquals("Company name cannot be blank.", exception.getMessage());
    }
}