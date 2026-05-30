package com.b2b.order.domain.valueobject;

import com.b2b.order.domain.exception.OrderDomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderQuantityTest {

    @Test
    void shouldCreateOrderQuantityWhenValueIsValid() {
        OrderQuantity quantity = OrderQuantity.of(new BigDecimal("10.00"));

        assertEquals(new BigDecimal("10"), quantity.getValue());
    }

    @Test
    void shouldCreateOrderQuantityFromString() {
        OrderQuantity quantity = OrderQuantity.of("5.50");

        assertEquals(new BigDecimal("5.5"), quantity.getValue());
    }

    @Test
    void shouldCreateOrderQuantityFromLong() {
        OrderQuantity quantity = OrderQuantity.of(3);

        assertEquals(new BigDecimal("3"), quantity.getValue());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> OrderQuantity.of((BigDecimal) null)
        );

        assertEquals("Order quantity cannot be null.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenValueIsZero() {
        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> OrderQuantity.of(BigDecimal.ZERO)
        );

        assertEquals("Order quantity must be greater than zero.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNegative() {
        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> OrderQuantity.of(new BigDecimal("-1"))
        );

        assertEquals("Order quantity must be greater than zero.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenValueHasMoreThanTwoDecimalPlaces() {
        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> OrderQuantity.of(new BigDecimal("1.123"))
        );

        assertEquals("Order quantity cannot have more than 2 decimal places.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStringValueIsBlank() {
        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> OrderQuantity.of(" ")
        );

        assertEquals("Order quantity cannot be blank.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStringValueIsInvalid() {
        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> OrderQuantity.of("abc")
        );

        assertEquals("Order quantity must be a valid number.", exception.getMessage());
    }

    @Test
    void shouldCompareQuantitiesIgnoringScale() {
        OrderQuantity first = OrderQuantity.of(new BigDecimal("10.00"));
        OrderQuantity second = OrderQuantity.of(new BigDecimal("10"));

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldReturnTrueWhenQuantityIsPositive() {
        OrderQuantity quantity = OrderQuantity.of(new BigDecimal("1"));

        assertTrue(quantity.isPositive());
    }
}