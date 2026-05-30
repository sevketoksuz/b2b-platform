package com.b2b.order.infrastructure.persistence.entity;

import com.b2b.order.domain.enumtype.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "buyer_company_id", nullable = false)
    private UUID buyerCompanyId;

    @Column(name = "seller_company_id", nullable = false)
    private UUID sellerCompanyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<OrderItemJpaEntity> items = new ArrayList<>();

    protected OrderJpaEntity() {
        // JPA needs a no-args constructor.
    }

    public OrderJpaEntity(
            UUID id,
            UUID buyerCompanyId,
            UUID sellerCompanyId,
            OrderStatus status,
            BigDecimal totalAmount,
            String currency,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime confirmedAt,
            LocalDateTime cancelledAt,
            LocalDateTime completedAt
    ) {
        this.id = id;
        this.buyerCompanyId = buyerCompanyId;
        this.sellerCompanyId = sellerCompanyId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.confirmedAt = confirmedAt;
        this.cancelledAt = cancelledAt;
        this.completedAt = completedAt;
    }

    public void addItem(OrderItemJpaEntity item) {
        items.add(item);
        item.setOrder(this);
    }

    public UUID getId() {
        return id;
    }

    public UUID getBuyerCompanyId() {
        return buyerCompanyId;
    }

    public UUID getSellerCompanyId() {
        return sellerCompanyId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public List<OrderItemJpaEntity> getItems() {
        return items;
    }
}