package com.b2b.order.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItemJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderJpaEntity order;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_name", nullable = false, length = 150)
    private String productName;

    @Column(name = "quantity", nullable = false, precision = 19, scale = 2)
    private BigDecimal quantity;

    @Column(name = "unit_price_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPriceAmount;

    @Column(name = "unit_price_currency", nullable = false, length = 3)
    private String unitPriceCurrency;

    @Column(name = "line_total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal lineTotalAmount;

    @Column(name = "line_total_currency", nullable = false, length = 3)
    private String lineTotalCurrency;

    protected OrderItemJpaEntity() {
        // JPA needs a no-args constructor.
    }

    public OrderItemJpaEntity(
            UUID id,
            UUID productId,
            String productName,
            BigDecimal quantity,
            BigDecimal unitPriceAmount,
            String unitPriceCurrency,
            BigDecimal lineTotalAmount,
            String lineTotalCurrency
    ) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPriceAmount = unitPriceAmount;
        this.unitPriceCurrency = unitPriceCurrency;
        this.lineTotalAmount = lineTotalAmount;
        this.lineTotalCurrency = lineTotalCurrency;
    }

    void setOrder(OrderJpaEntity order) {
        this.order = order;
    }

    public UUID getId() {
        return id;
    }

    public OrderJpaEntity getOrder() {
        return order;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPriceAmount() {
        return unitPriceAmount;
    }

    public String getUnitPriceCurrency() {
        return unitPriceCurrency;
    }

    public BigDecimal getLineTotalAmount() {
        return lineTotalAmount;
    }

    public String getLineTotalCurrency() {
        return lineTotalCurrency;
    }
}