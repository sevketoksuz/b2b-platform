package com.b2b.inventory.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "processed_order_events")
public class ProcessedOrderEventJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "event_type", nullable = false, length = 80)
    private String eventType;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    protected ProcessedOrderEventJpaEntity() {
        // JPA needs a no-args constructor.
    }

    public ProcessedOrderEventJpaEntity(
            UUID id,
            String eventType,
            UUID orderId,
            UUID productId,
            LocalDateTime processedAt
    ) {
        this.id = id;
        this.eventType = eventType;
        this.orderId = orderId;
        this.productId = productId;
        this.processedAt = processedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getEventType() {
        return eventType;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getProductId() {
        return productId;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
}