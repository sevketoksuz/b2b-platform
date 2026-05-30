package com.b2b.inventory.domain.model;

import com.b2b.inventory.domain.enumtype.StockMovementType;
import com.b2b.inventory.domain.exception.StockDomainException;
import com.b2b.inventory.domain.valueobject.Quantity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class StockMovement {

    private final UUID id;
    private final UUID companyId;
    private final UUID productId;
    private final StockMovementType movementType;
    private final Quantity quantity;
    private final Quantity previousQuantity;
    private final Quantity newQuantity;
    private final String reason;
    private final LocalDateTime createdAt;

    private StockMovement(
            UUID id,
            UUID companyId,
            UUID productId,
            StockMovementType movementType,
            Quantity quantity,
            Quantity previousQuantity,
            Quantity newQuantity,
            String reason,
            LocalDateTime createdAt
    ) {
        validateMovementType(movementType);
        validateReason(reason);

        this.id = Objects.requireNonNull(id, "Stock movement id cannot be null.");
        this.companyId = Objects.requireNonNull(companyId, "Company id cannot be null.");
        this.productId = Objects.requireNonNull(productId, "Product id cannot be null.");
        this.movementType = movementType;
        this.quantity = Objects.requireNonNull(quantity, "Movement quantity cannot be null.");
        this.previousQuantity = Objects.requireNonNull(previousQuantity, "Previous quantity cannot be null.");
        this.newQuantity = Objects.requireNonNull(newQuantity, "New quantity cannot be null.");
        this.reason = normalizeReason(reason);
        this.createdAt = Objects.requireNonNull(createdAt, "Created date cannot be null.");
    }

    public static StockMovement createIncrease(
            UUID companyId,
            UUID productId,
            Quantity quantity,
            Quantity previousQuantity,
            Quantity newQuantity,
            String reason
    ) {
        validatePositiveMovementQuantity(quantity);

        Quantity expectedNewQuantity = previousQuantity.add(quantity);

        if (!expectedNewQuantity.equals(newQuantity)) {
            throw new StockDomainException("Invalid stock movement quantities for increase.");
        }

        return new StockMovement(
                UUID.randomUUID(),
                companyId,
                productId,
                StockMovementType.INCREASE,
                quantity,
                previousQuantity,
                newQuantity,
                reason,
                LocalDateTime.now()
        );
    }

    public static StockMovement createDecrease(
            UUID companyId,
            UUID productId,
            Quantity quantity,
            Quantity previousQuantity,
            Quantity newQuantity,
            String reason
    ) {
        validatePositiveMovementQuantity(quantity);

        Quantity expectedNewQuantity = previousQuantity.subtract(quantity);

        if (!expectedNewQuantity.equals(newQuantity)) {
            throw new StockDomainException("Invalid stock movement quantities for decrease.");
        }

        return new StockMovement(
                UUID.randomUUID(),
                companyId,
                productId,
                StockMovementType.DECREASE,
                quantity,
                previousQuantity,
                newQuantity,
                reason,
                LocalDateTime.now()
        );
    }

    public static StockMovement createAdjustment(
            UUID companyId,
            UUID productId,
            Quantity previousQuantity,
            Quantity newQuantity,
            String reason
    ) {
        Objects.requireNonNull(previousQuantity, "Previous quantity cannot be null.");
        Objects.requireNonNull(newQuantity, "New quantity cannot be null.");

        if (previousQuantity.equals(newQuantity)) {
            throw new StockDomainException("Adjustment must change stock quantity.");
        }

        Quantity movementQuantity = previousQuantity.difference(newQuantity);

        return new StockMovement(
                UUID.randomUUID(),
                companyId,
                productId,
                StockMovementType.ADJUSTMENT,
                movementQuantity,
                previousQuantity,
                newQuantity,
                reason,
                LocalDateTime.now()
        );
    }

    public static StockMovement restore(
            UUID id,
            UUID companyId,
            UUID productId,
            StockMovementType movementType,
            Quantity quantity,
            Quantity previousQuantity,
            Quantity newQuantity,
            String reason,
            LocalDateTime createdAt
    ) {
        return new StockMovement(
                id,
                companyId,
                productId,
                movementType,
                quantity,
                previousQuantity,
                newQuantity,
                reason,
                createdAt
        );
    }

    private static void validatePositiveMovementQuantity(Quantity quantity) {
        Objects.requireNonNull(quantity, "Movement quantity cannot be null.");

        if (!quantity.isPositive()) {
            throw new StockDomainException("Movement quantity must be greater than zero.");
        }
    }

    private void validateMovementType(StockMovementType movementType) {
        if (movementType == null) {
            throw new StockDomainException("Stock movement type cannot be null.");
        }
    }

    private void validateReason(String reason) {
        if (reason != null && reason.trim().length() > 255) {
            throw new StockDomainException("Stock movement reason cannot exceed 255 characters.");
        }
    }

    private String normalizeReason(String reason) {
        if (reason == null || reason.isBlank()) {
            return null;
        }

        return reason.trim();
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

    public StockMovementType getMovementType() {
        return movementType;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public Quantity getPreviousQuantity() {
        return previousQuantity;
    }

    public Quantity getNewQuantity() {
        return newQuantity;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}