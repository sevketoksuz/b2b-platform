package com.b2b.inventory.domain.valueobject;

import com.b2b.inventory.domain.exception.ProductDomainException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SkuTest {

    @Test
    void shouldCreateSkuWhenValueIsValid() {
        Sku sku = Sku.of("LAPTOP-001");

        assertEquals("LAPTOP-001", sku.getValue());
    }

    @Test
    void shouldNormalizeSku() {
        Sku sku = Sku.of("  laptop-001  ");

        assertEquals("LAPTOP-001", sku.getValue());
    }

    @Test
    void shouldThrowExceptionWhenSkuIsBlank() {
        ProductDomainException exception = assertThrows(
                ProductDomainException.class,
                () -> Sku.of("")
        );

        assertEquals("SKU cannot be blank.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSkuIsTooShort() {
        ProductDomainException exception = assertThrows(
                ProductDomainException.class,
                () -> Sku.of("AB")
        );

        assertEquals("SKU must contain at least 3 characters.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSkuContainsInvalidCharacters() {
        ProductDomainException exception = assertThrows(
                ProductDomainException.class,
                () -> Sku.of("LAPTOP 001")
        );

        assertEquals("SKU can contain only letters, numbers, dot, underscore and hyphen.", exception.getMessage());
    }
}