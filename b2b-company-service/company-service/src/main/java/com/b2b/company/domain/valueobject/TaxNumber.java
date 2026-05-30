package com.b2b.company.domain.valueobject;

import com.b2b.company.domain.exception.CompanyDomainException;

import java.util.Objects;

public final class TaxNumber {

    private final String value;

    private TaxNumber(String value) {
        validate(value);
        this.value = value;
    }

    public static TaxNumber of(String value) {
        return new TaxNumber(value);
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new CompanyDomainException("Tax number cannot be blank.");
        }

        String normalizedValue = value.trim();

        if (!normalizedValue.matches("\\d{10,11}")) {
            throw new CompanyDomainException("Tax number must contain 10 or 11 digits.");
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof TaxNumber taxNumber)) {
            return false;
        }

        return Objects.equals(value, taxNumber.value);
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