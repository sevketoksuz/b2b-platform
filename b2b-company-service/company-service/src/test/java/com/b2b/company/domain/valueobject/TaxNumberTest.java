package com.b2b.company.domain.valueobject;

import com.b2b.company.domain.exception.CompanyDomainException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaxNumberTest {

    @Test
    void shouldCreateTaxNumberWhenValueIsValid10Digits() {
        TaxNumber taxNumber = TaxNumber.of("1234567890");

        assertEquals("1234567890", taxNumber.getValue());
    }

    @Test
    void shouldCreateTaxNumberWhenValueIsValid11Digits() {
        TaxNumber taxNumber = TaxNumber.of("12345678901");

        assertEquals("12345678901", taxNumber.getValue());
    }

    @Test
    void shouldThrowExceptionWhenTaxNumberIsBlank() {
        CompanyDomainException exception = assertThrows(
                CompanyDomainException.class,
                () -> TaxNumber.of("")
        );

        assertEquals("Tax number cannot be blank.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTaxNumberContainsLetters() {
        CompanyDomainException exception = assertThrows(
                CompanyDomainException.class,
                () -> TaxNumber.of("12345abc90")
        );

        assertEquals("Tax number must contain 10 or 11 digits.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTaxNumberLengthIsInvalid() {
        CompanyDomainException exception = assertThrows(
                CompanyDomainException.class,
                () -> TaxNumber.of("123")
        );

        assertEquals("Tax number must contain 10 or 11 digits.", exception.getMessage());
    }
}