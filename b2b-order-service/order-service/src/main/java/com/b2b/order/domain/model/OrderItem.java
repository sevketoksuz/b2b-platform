package com.b2b.order.domain.model;

import com.b2b.order.domain.exception.OrderDomainException;
import com.b2b.order.domain.valueobject.Money;
import com.b2b.order.domain.valueobject.OrderQuantity;

import java.util.Objects;
import java.util.UUID;

public class OrderItem {

    private final UUID id;
    private final UUID productId;
    private final String productName;
    private final OrderQuantity quantity;
    private final Money unitPrice;
    private final Money lineTotal;

    private OrderItem(
            UUID id,
            UUID productId,
            String productName,
            OrderQuantity quantity,
            Money unitPrice,
            Money lineTotal
    ) {
        validateProductName(productName);
        validateLineTotal(quantity, unitPrice, lineTotal);

        this.id = Objects.requireNonNull(id, "Order item id cannot be null.");
        this.productId = Objects.requireNonNull(productId, "Product id cannot be null.");
        this.productName = productName.trim();
        this.quantity = Objects.requireNonNull(quantity, "Order item quantity cannot be null.");
        this.unitPrice = Objects.requireNonNull(unitPrice, "Order item unit price cannot be null.");
        this.lineTotal = Objects.requireNonNull(lineTotal, "Order item line total cannot be null.");
    }

    public static OrderItem create(
            UUID productId,
            String productName,
            OrderQuantity quantity,
            Money unitPrice
    ) {
        Money lineTotal = unitPrice.multiply(quantity);

        return new OrderItem(
                UUID.randomUUID(),
                productId,
                productName,
                quantity,
                unitPrice,
                lineTotal
        );
    }

    public static OrderItem restore(
            UUID id,
            UUID productId,
            String productName,
            OrderQuantity quantity,
            Money unitPrice,
            Money lineTotal
    ) {
        return new OrderItem(
                id,
                productId,
                productName,
                quantity,
                unitPrice,
                lineTotal
        );
    }

    private void validateProductName(String productName) {
        if (productName == null || productName.isBlank()) {
            throw new OrderDomainException("Product name cannot be blank.");
        }

        if (productName.trim().length() < 2) {
            throw new OrderDomainException("Product name must contain at least 2 characters.");
        }

        if (productName.trim().length() > 150) {
            throw new OrderDomainException("Product name cannot exceed 150 characters.");
        }
    }

    private void validateLineTotal(
            OrderQuantity quantity,
            Money unitPrice,
            Money lineTotal
    ) {
        Objects.requireNonNull(quantity, "Order item quantity cannot be null.");
        Objects.requireNonNull(unitPrice, "Order item unit price cannot be null.");
        Objects.requireNonNull(lineTotal, "Order item line total cannot be null.");

        if (!unitPrice.isPositive()) {
            throw new OrderDomainException("Order item unit price must be greater than zero.");
        }

        Money expectedLineTotal = unitPrice.multiply(quantity);

        if (!expectedLineTotal.equals(lineTotal)) {
            throw new OrderDomainException("Order item line total is invalid.");
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public OrderQuantity getQuantity() {
        return quantity;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public Money getLineTotal() {
        return lineTotal;
    }
}