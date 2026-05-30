package com.b2b.inventory.domain.valueobject;

import com.b2b.inventory.domain.exception.StockDomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class QuantityTest {

    @Test
    void shouldCreateQuantityWhenValueIsValid() {
        Quantity quantity = Quantity.of(BigDecimal.valueOf(10));

        assertEquals(0, new BigDecimal("10").compareTo(quantity.getValue()));
    }

    @Test
    void shouldCreateQuantityFromString() {
        Quantity quantity = Quantity.of("15.50");

        assertEquals(new BigDecimal("15.5"), quantity.getValue());
    }

    @Test
    void shouldCreateZeroQuantity() {
        Quantity quantity = Quantity.zero();

        assertTrue(quantity.isZero());
        assertFalse(quantity.isPositive());
    }

    @Test
    void shouldAddQuantities() {
        Quantity first = Quantity.of("10.50");
        Quantity second = Quantity.of("5.25");

        Quantity result = first.add(second);

        assertEquals(Quantity.of("15.75"), result);
    }

    @Test
    void shouldSubtractQuantities() {
        Quantity first = Quantity.of("10.50");
        Quantity second = Quantity.of("5.25");

        Quantity result = first.subtract(second);

        assertEquals(Quantity.of("5.25"), result);
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsNegative() {
        StockDomainException exception = assertThrows(
                StockDomainException.class,
                () -> Quantity.of(BigDecimal.valueOf(-1))
        );

        assertEquals("Quantity cannot be negative.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenQuantityHasMoreThanTwoDecimalPlaces() {
        StockDomainException exception = assertThrows(
                StockDomainException.class,
                () -> Quantity.of("10.555")
        );

        assertEquals("Quantity cannot have more than 2 decimal places.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSubtractionResultIsNegative() {
        Quantity first = Quantity.of(5);
        Quantity second = Quantity.of(10);

        StockDomainException exception = assertThrows(
                StockDomainException.class,
                () -> first.subtract(second)
        );

        assertEquals("Stock quantity cannot be negative.", exception.getMessage());
    }

    @Test
    void shouldCompareQuantitiesByNumericValue() {
        Quantity first = Quantity.of("10.00");
        Quantity second = Quantity.of("10");

        assertEquals(first, second);
    }
}