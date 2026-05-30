package com.b2b.inventory.domain.model;

import com.b2b.inventory.domain.exception.StockDomainException;
import com.b2b.inventory.domain.valueobject.Quantity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Stock {

    private final UUID id;
    private final UUID companyId;
    private final UUID productId;
    private Quantity quantity;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Stock(
            UUID id,
            UUID companyId,
            UUID productId,
            Quantity quantity,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "Stock id cannot be null.");
        this.companyId = Objects.requireNonNull(companyId, "Company id cannot be null.");
        this.productId = Objects.requireNonNull(productId, "Product id cannot be null.");
        this.quantity = Objects.requireNonNull(quantity, "Quantity cannot be null.");
        this.createdAt = Objects.requireNonNull(createdAt, "Created date cannot be null.");
        this.updatedAt = updatedAt;
    }

    public static Stock create(
            UUID companyId,
            UUID productId
    ) {
        LocalDateTime now = LocalDateTime.now();

        return new Stock(
                UUID.randomUUID(),
                companyId,
                productId,
                Quantity.zero(),
                now,
                now
        );
    }

    public static Stock restore(
            UUID id,
            UUID companyId,
            UUID productId,
            Quantity quantity,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Stock(
                id,
                companyId,
                productId,
                quantity,
                createdAt,
                updatedAt
        );
    }

    public void increase(Quantity amount) {
        validatePositiveAmount(amount);

        this.quantity = this.quantity.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public void decrease(Quantity amount) {
        validatePositiveAmount(amount);

        if (this.quantity.isLessThan(amount)) {
            throw new StockDomainException("Insufficient stock quantity.");
        }

        this.quantity = this.quantity.subtract(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public void adjust(Quantity newQuantity) {
        Objects.requireNonNull(newQuantity, "New quantity cannot be null.");

        if (this.quantity.equals(newQuantity)) {
            throw new StockDomainException("New quantity is same as current quantity.");
        }

        this.quantity = newQuantity;
        this.updatedAt = LocalDateTime.now();
    }

    private void validatePositiveAmount(Quantity amount) {
        Objects.requireNonNull(amount, "Amount cannot be null.");

        if (!amount.isPositive()) {
            throw new StockDomainException("Amount must be greater than zero.");
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public UUID getProductId() {
        return productId;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}