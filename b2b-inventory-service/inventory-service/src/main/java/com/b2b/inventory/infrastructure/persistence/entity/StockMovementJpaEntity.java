package com.b2b.inventory.infrastructure.persistence.entity;

import com.b2b.inventory.domain.enumtype.StockMovementType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_movements")
public class StockMovementJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 30)
    private StockMovementType movementType;

    @Column(name = "quantity", nullable = false, precision = 19, scale = 2)
    private BigDecimal quantity;

    @Column(name = "previous_quantity", nullable = false, precision = 19, scale = 2)
    private BigDecimal previousQuantity;

    @Column(name = "new_quantity", nullable = false, precision = 19, scale = 2)
    private BigDecimal newQuantity;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected StockMovementJpaEntity() {
        // JPA needs a no-args constructor.
    }

    public StockMovementJpaEntity(
            UUID id,
            UUID companyId,
            UUID productId,
            StockMovementType movementType,
            BigDecimal quantity,
            BigDecimal previousQuantity,
            BigDecimal newQuantity,
            String reason,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.companyId = companyId;
        this.productId = productId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.previousQuantity = previousQuantity;
        this.newQuantity = newQuantity;
        this.reason = reason;
        this.createdAt = createdAt;
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getPreviousQuantity() {
        return previousQuantity;
    }

    public BigDecimal getNewQuantity() {
        return newQuantity;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}