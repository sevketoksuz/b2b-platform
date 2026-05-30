package com.b2b.company.domain.valueobject;

import com.b2b.company.domain.exception.CompanyMemberDomainException;

import java.util.Objects;

public final class Email {

    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    private final String value;

    private Email(String value) {
        validate(value);
        this.value = normalize(value);
    }

    public static Email of(String value) {
        return new Email(value);
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new CompanyMemberDomainException("Email cannot be blank.");
        }

        String normalizedValue = normalize(value);

        if (normalizedValue.length() > 150) {
            throw new CompanyMemberDomainException("Email cannot exceed 150 characters.");
        }

        if (!normalizedValue.matches(EMAIL_REGEX)) {
            throw new CompanyMemberDomainException("Email format is invalid.");
        }
    }

    private String normalize(String value) {
        return value.trim().toLowerCase();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Email email)) {
            return false;
        }

        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}