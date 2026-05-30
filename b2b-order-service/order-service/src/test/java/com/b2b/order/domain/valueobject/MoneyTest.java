package com.b2b.order.domain.valueobject;

import com.b2b.order.domain.exception.OrderDomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void shouldCreateMoneyWhenAmountAndCurrencyAreValid() {
        Money money = Money.of(new BigDecimal("100.00"), "try");

        assertEquals(new BigDecimal("100"), money.getAmount());
        assertEquals("TRY", money.getCurrency());
    }

    @Test
    void shouldNormalizeAmountWithoutScientificNotationProblem() {
        Money money = Money.of(BigDecimal.valueOf(1000), "TRY");

        assertEquals("1000", money.getAmount().toPlainString());
        assertEquals("1000 TRY", money.toString());
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNull() {
        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> Money.of((BigDecimal)null, "TRY")
        );

        assertEquals("Money amount cannot be null.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNegative() {
        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> Money.of(new BigDecimal("-1"), "TRY")
        );

        assertEquals("Money amount cannot be negative.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAmountHasMoreThanTwoDecimalPlaces() {
        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> Money.of(new BigDecimal("10.123"), "TRY")
        );

        assertEquals("Money amount cannot have more than 2 decimal places.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCurrencyIsBlank() {
        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> Money.of(new BigDecimal("10"), " ")
        );

        assertEquals("Money currency cannot be blank.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCurrencyFormatIsInvalid() {
        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> Money.of(new BigDecimal("10"), "TL")
        );

        assertEquals("Money currency must be a valid 3-letter currency code.", exception.getMessage());
    }

    @Test
    void shouldAddMoneyWhenCurrenciesAreSame() {
        Money first = Money.of(new BigDecimal("100"), "TRY");
        Money second = Money.of(new BigDecimal("50"), "TRY");

        Money result = first.add(second);

        assertEquals(new BigDecimal("150"), result.getAmount());
        assertEquals("TRY", result.getCurrency());
    }

    @Test
    void shouldThrowExceptionWhenAddingDifferentCurrencies() {
        Money first = Money.of(new BigDecimal("100"), "TRY");
        Money second = Money.of(new BigDecimal("50"), "USD");

        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> first.add(second)
        );

        assertEquals("Money currencies must be same.", exception.getMessage());
    }

    @Test
    void shouldMultiplyMoneyByQuantity() {
        Money unitPrice = Money.of(new BigDecimal("750.00"), "TRY");
        OrderQuantity quantity = OrderQuantity.of(new BigDecimal("2"));

        Money result = unitPrice.multiply(quantity);

        assertEquals(new BigDecimal("1500"), result.getAmount());
        assertEquals("TRY", result.getCurrency());
    }

    @Test
    void shouldCompareMoneyByAmountAndCurrencyIgnoringScale() {
        Money first = Money.of(new BigDecimal("10.00"), "TRY");
        Money second = Money.of(new BigDecimal("10"), "TRY");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}