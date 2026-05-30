package com.b2b.company.domain.valueobject;

import com.b2b.company.domain.exception.CompanyMemberDomainException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void shouldCreateEmailWhenValueIsValid() {
        Email email = Email.of("john@acme.com");

        assertEquals("john@acme.com", email.getValue());
    }

    @Test
    void shouldNormalizeEmail() {
        Email email = Email.of("  John@Acme.COM  ");

        assertEquals("john@acme.com", email.getValue());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        CompanyMemberDomainException exception = assertThrows(
                CompanyMemberDomainException.class,
                () -> Email.of("")
        );

        assertEquals("Email cannot be blank.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailFormatIsInvalid() {
        CompanyMemberDomainException exception = assertThrows(
                CompanyMemberDomainException.class,
                () -> Email.of("wrong-email")
        );

        assertEquals("Email format is invalid.", exception.getMessage());
    }
}