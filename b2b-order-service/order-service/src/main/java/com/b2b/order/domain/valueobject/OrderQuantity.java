package com.b2b.order.domain.valueobject;

import com.b2b.order.domain.exception.OrderDomainException;

import java.math.BigDecimal;
import java.util.Objects;

public final class OrderQuantity implements Comparable<OrderQuantity> {

    private final BigDecimal value;

    private OrderQuantity(BigDecimal value) {
        validate(value);
        this.value = normalize(value);
    }

    public static OrderQuantity of(BigDecimal value) {
        return new OrderQuantity(value);
    }

    public static OrderQuantity of(String value) {
        if (value == null || value.isBlank()) {
            throw new OrderDomainException("Order quantity cannot be blank.");
        }

        try {
            return new OrderQuantity(new BigDecimal(value));
        } catch (NumberFormatException exception) {
            throw new OrderDomainException("Order quantity must be a valid number.");
        }
    }

    public static OrderQuantity of(long value) {
        return new OrderQuantity(BigDecimal.valueOf(value));
    }

    private void validate(BigDecimal value) {
        if (value == null) {
            throw new OrderDomainException("Order quantity cannot be null.");
        }

        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OrderDomainException("Order quantity must be greater than zero.");
        }

        if (value.scale() > 2) {
            throw new OrderDomainException("Order quantity cannot have more than 2 decimal places.");
        }

        if (value.precision() > 19) {
            throw new OrderDomainException("Order quantity cannot exceed 19 digits.");
        }
    }

    private BigDecimal normalize(BigDecimal value) {
        BigDecimal normalizedValue = value.stripTrailingZeros();

        if (normalizedValue.scale() < 0) {
            return normalizedValue.setScale(0);
        }

        return normalizedValue;
    }

    public BigDecimal getValue() {
        return value;
    }

    public boolean isPositive() {
        return value.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public int compareTo(OrderQuantity other) {
        return this.value.compareTo(other.value);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof OrderQuantity quantity)) {
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
}