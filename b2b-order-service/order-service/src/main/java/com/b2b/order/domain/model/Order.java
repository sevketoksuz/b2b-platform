package com.b2b.order.domain.model;

import com.b2b.order.domain.enumtype.OrderStatus;
import com.b2b.order.domain.exception.OrderDomainException;
import com.b2b.order.domain.valueobject.Money;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Order {

    private final UUID id;
    private final UUID buyerCompanyId;
    private final UUID sellerCompanyId;
    private final List<OrderItem> items;
    private OrderStatus status;
    private Money totalAmount;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime completedAt;

    private Order(
            UUID id,
            UUID buyerCompanyId,
            UUID sellerCompanyId,
            List<OrderItem> items,
            OrderStatus status,
            Money totalAmount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime confirmedAt,
            LocalDateTime cancelledAt,
            LocalDateTime completedAt
    ) {
        validateCompanies(buyerCompanyId, sellerCompanyId);
        validateItems(items);
        validateStatus(status);
        validateTotalAmount(items, totalAmount);

        this.id = Objects.requireNonNull(id, "Order id cannot be null.");
        this.buyerCompanyId = buyerCompanyId;
        this.sellerCompanyId = sellerCompanyId;
        this.items = List.copyOf(items);
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = Objects.requireNonNull(createdAt, "Created date cannot be null.");
        this.updatedAt = updatedAt;
        this.confirmedAt = confirmedAt;
        this.cancelledAt = cancelledAt;
        this.completedAt = completedAt;
    }

    public static Order create(
            UUID buyerCompanyId,
            UUID sellerCompanyId,
            List<OrderItem> items
    ) {
        LocalDateTime now = LocalDateTime.now();
        Money totalAmount = calculateTotalAmount(items);

        return new Order(
                UUID.randomUUID(),
                buyerCompanyId,
                sellerCompanyId,
                items,
                OrderStatus.PENDING,
                totalAmount,
                now,
                now,
                null,
                null,
                null
        );
    }

    public static Order restore(
            UUID id,
            UUID buyerCompanyId,
            UUID sellerCompanyId,
            List<OrderItem> items,
            OrderStatus status,
            Money totalAmount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime confirmedAt,
            LocalDateTime cancelledAt,
            LocalDateTime completedAt
    ) {
        return new Order(
                id,
                buyerCompanyId,
                sellerCompanyId,
                items,
                status,
                totalAmount,
                createdAt,
                updatedAt,
                confirmedAt,
                cancelledAt,
                completedAt
        );
    }

    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new OrderDomainException("Only pending orders can be confirmed.");
        }

        LocalDateTime now = LocalDateTime.now();

        this.status = OrderStatus.CONFIRMED;
        this.confirmedAt = now;
        this.updatedAt = now;
    }

    public void cancel() {
        if (this.status != OrderStatus.PENDING) {
            throw new OrderDomainException("Only pending orders can be cancelled.");
        }

        LocalDateTime now = LocalDateTime.now();

        this.status = OrderStatus.CANCELLED;
        this.cancelledAt = now;
        this.updatedAt = now;
    }

    public void complete() {
        if (this.status != OrderStatus.CONFIRMED) {
            throw new OrderDomainException("Only confirmed orders can be completed.");
        }

        LocalDateTime now = LocalDateTime.now();

        this.status = OrderStatus.COMPLETED;
        this.completedAt = now;
        this.updatedAt = now;
    }

    public boolean isPending() {
        return this.status == OrderStatus.PENDING;
    }

    public boolean isConfirmed() {
        return this.status == OrderStatus.CONFIRMED;
    }

    private void validateCompanies(UUID buyerCompanyId, UUID sellerCompanyId) {
        if (buyerCompanyId == null) {
            throw new OrderDomainException("Buyer company id cannot be null.");
        }

        if (sellerCompanyId == null) {
            throw new OrderDomainException("Seller company id cannot be null.");
        }

        if (buyerCompanyId.equals(sellerCompanyId)) {
            throw new OrderDomainException("Buyer company and seller company cannot be same.");
        }
    }

    private void validateItems(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new OrderDomainException("Order must contain at least one item.");
        }

        Set<UUID> productIds = new HashSet<>();

        for (OrderItem item : items) {
            if (item == null) {
                throw new OrderDomainException("Order item cannot be null.");
            }

            if (!productIds.add(item.getProductId())) {
                throw new OrderDomainException("Order cannot contain duplicate product items.");
            }
        }

        validateSingleCurrency(items);
    }

    private void validateSingleCurrency(List<OrderItem> items) {
        String currency = items.getFirst().getLineTotal().getCurrency();

        for (OrderItem item : items) {
            if (!currency.equals(item.getLineTotal().getCurrency())) {
                throw new OrderDomainException("All order items must have same currency.");
            }
        }
    }

    private void validateStatus(OrderStatus status) {
        if (status == null) {
            throw new OrderDomainException("Order status cannot be null.");
        }
    }

    private void validateTotalAmount(List<OrderItem> items, Money totalAmount) {
        Objects.requireNonNull(totalAmount, "Order total amount cannot be null.");

        Money expectedTotalAmount = calculateTotalAmount(items);

        if (!expectedTotalAmount.equals(totalAmount)) {
            throw new OrderDomainException("Order total amount is invalid.");
        }
    }

    private static Money calculateTotalAmount(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new OrderDomainException("Order must contain at least one item.");
        }

        OrderItem firstItem = items.getFirst();

        if (firstItem == null) {
            throw new OrderDomainException("Order item cannot be null.");
        }

        String currency = firstItem.getLineTotal().getCurrency();

        Money total = Money.zero(currency);

        for (OrderItem item : items) {
            if (item == null) {
                throw new OrderDomainException("Order item cannot be null.");
            }

            if (!currency.equals(item.getLineTotal().getCurrency())) {
                throw new OrderDomainException("All order items must have same currency.");
            }

            total = total.add(item.getLineTotal());
        }

        return total;
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

    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Money getTotalAmount() {
        return totalAmount;
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
}