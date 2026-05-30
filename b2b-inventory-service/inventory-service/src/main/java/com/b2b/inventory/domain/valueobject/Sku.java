package com.b2b.inventory.domain.valueobject;

import com.b2b.inventory.domain.exception.ProductDomainException;

import java.util.Locale;
import java.util.Objects;

public final class Sku {

    private final String value;

    private Sku(String value) {
        this.value = normalize(value);
        validate(this.value);
    }

    public static Sku of(String value) {
        return new Sku(value);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        return value.trim().toUpperCase(Locale.ROOT);
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new ProductDomainException("SKU cannot be blank.");
        }

        if (value.length() < 3) {
            throw new ProductDomainException("SKU must contain at least 3 characters.");
        }

        if (value.length() > 50) {
            throw new ProductDomainException("SKU cannot exceed 50 characters.");
        }

        if (!value.matches("^[A-Z0-9._-]+$")) {
            throw new ProductDomainException("SKU can contain only letters, numbers, dot, underscore and hyphen.");
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

        if (!(object instanceof Sku sku)) {
            return false;
        }

        return Objects.equals(value, sku.value);
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