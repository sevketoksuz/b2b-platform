package com.b2b.inventory.domain.valueobject;

import com.b2b.inventory.domain.exception.StockDomainException;

import java.math.BigDecimal;
import java.util.Objects;


public final class Quantity implements Comparable<Quantity> {

    private final BigDecimal value;

    private Quantity(BigDecimal value) {
        validate(value);
        this.value = normalize(value);
    }

    public static Quantity of(BigDecimal value) {
        return new Quantity(value);
    }

    public static Quantity of(String value) {
        if (value == null || value.isBlank()) {
            throw new StockDomainException("Quantity cannot be blank.");
        }

        try {
            return new Quantity(new BigDecimal(value));
        } catch (NumberFormatException exception) {
            throw new StockDomainException("Quantity must be a valid number.");
        }
    }

    public static Quantity of(long value) {
        return new Quantity(BigDecimal.valueOf(value));
    }

    public static Quantity zero() {
        return new Quantity(BigDecimal.ZERO);
    }

    private void validate(BigDecimal value) {
        if (value == null) {
            throw new StockDomainException("Quantity cannot be null.");
        }

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new StockDomainException("Quantity cannot be negative.");
        }

        if (value.scale() > 2) {
            throw new StockDomainException("Quantity cannot have more than 2 decimal places.");
        }

        if (value.precision() > 19) {
            throw new StockDomainException("Quantity cannot exceed 19 digits.");
        }
    }

    public Quantity add(Quantity other) {
        Objects.requireNonNull(other, "Quantity to add cannot be null.");

        return Quantity.of(this.value.add(other.value));
    }

    public Quantity subtract(Quantity other) {
        Objects.requireNonNull(other, "Quantity to subtract cannot be null.");

        BigDecimal result = this.value.subtract(other.value);

        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new StockDomainException("Stock quantity cannot be negative.");
        }

        return Quantity.of(result);
    }

    public Quantity difference(Quantity other) {
        Objects.requireNonNull(other, "Quantity to compare cannot be null.");

        BigDecimal difference = this.value.subtract(other.value).abs();

        return Quantity.of(difference);
    }

    public boolean isZero() {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return value.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isLessThan(Quantity other) {
        Objects.requireNonNull(other, "Quantity to compare cannot be null.");

        return this.value.compareTo(other.value) < 0;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public int compareTo(Quantity other) {
        return this.value.compareTo(other.value);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Quantity quantity)) {
            return false;
        }

        return value.compareTo(quantity.value) == 0;
    }

    @Override
    public int hashCode() {
        return value.stripTrailingZeros().hashCode();
    }

    @Override
    public String toString() {
        return value.toPlainString();
    }

    private BigDecimal normalize(BigDecimal value) {
        BigDecimal normalizedValue = value.stripTrailingZeros();

        if (normalizedValue.scale() < 0) {
            return normalizedValue.setScale(0);
        }

        return normalizedValue;
    }
}