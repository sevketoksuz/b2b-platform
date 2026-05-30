package com.b2b.order.domain.valueobject;

import com.b2b.order.domain.exception.OrderDomainException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Money {

    private final BigDecimal amount;
    private final String currency;

    private Money(BigDecimal amount, String currency) {
        validateAmount(amount);
        validateCurrency(currency);

        this.amount = normalize(amount);
        this.currency = normalizeCurrency(currency);
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public static Money of(String amount, String currency) {
        if (amount == null || amount.isBlank()) {
            throw new OrderDomainException("Money amount cannot be blank.");
        }

        try {
            return new Money(new BigDecimal(amount), currency);
        } catch (NumberFormatException exception) {
            throw new OrderDomainException("Money amount must be a valid number.");
        }
    }

    public static Money zero(String currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new OrderDomainException("Money amount cannot be null.");
        }

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new OrderDomainException("Money amount cannot be negative.");
        }

        if (amount.scale() > 2) {
            throw new OrderDomainException("Money amount cannot have more than 2 decimal places.");
        }

        if (amount.precision() > 19) {
            throw new OrderDomainException("Money amount cannot exceed 19 digits.");
        }
    }

    private void validateCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            throw new OrderDomainException("Money currency cannot be blank.");
        }

        String normalizedCurrency = normalizeCurrency(currency);

        if (!normalizedCurrency.matches("[A-Z]{3}")) {
            throw new OrderDomainException("Money currency must be a valid 3-letter currency code.");
        }
    }

    private BigDecimal normalize(BigDecimal amount) {
        BigDecimal normalizedAmount = amount.stripTrailingZeros();

        if (normalizedAmount.scale() < 0) {
            return normalizedAmount.setScale(0);
        }

        return normalizedAmount;
    }

    private String normalizeCurrency(String currency) {
        return currency.trim().toUpperCase();
    }

    public Money add(Money other) {
        Objects.requireNonNull(other, "Money to add cannot be null.");
        validateSameCurrency(other);

        return Money.of(this.amount.add(other.amount), this.currency);
    }

    public Money multiply(OrderQuantity quantity) {
        Objects.requireNonNull(quantity, "Quantity cannot be null.");

        BigDecimal result = this.amount
                .multiply(quantity.getValue())
                .setScale(2, RoundingMode.HALF_UP);

        return Money.of(result, this.currency);
    }

    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    private void validateSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new OrderDomainException("Money currencies must be same.");
        }
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Money money)) {
            return false;
        }

        return amount.compareTo(money.amount) == 0
                && Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros(), currency);
    }

    @Override
    public String toString() {
        return amount.toPlainString() + " " + currency;
    }
}