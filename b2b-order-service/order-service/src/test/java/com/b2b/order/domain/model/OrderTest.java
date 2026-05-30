package com.b2b.order.domain.model;

import com.b2b.order.domain.enumtype.OrderStatus;
import com.b2b.order.domain.exception.OrderDomainException;
import com.b2b.order.domain.valueobject.Money;
import com.b2b.order.domain.valueobject.OrderQuantity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void shouldCreateOrderWhenValuesAreValid() {
        UUID buyerCompanyId = UUID.randomUUID();
        UUID sellerCompanyId = UUID.randomUUID();

        OrderItem firstItem = orderItem(UUID.randomUUID(), "Gaming Mouse", 2, "750");
        OrderItem secondItem = orderItem(UUID.randomUUID(), "Keyboard", 1, "1000");

        Order order = Order.create(
                buyerCompanyId,
                sellerCompanyId,
                List.of(firstItem, secondItem)
        );

        assertNotNull(order.getId());
        assertEquals(buyerCompanyId, order.getBuyerCompanyId());
        assertEquals(sellerCompanyId, order.getSellerCompanyId());
        assertEquals(2, order.getItems().size());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(Money.of(new BigDecimal("2500"), "TRY"), order.getTotalAmount());
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
        assertNull(order.getConfirmedAt());
        assertNull(order.getCancelledAt());
        assertNull(order.getCompletedAt());
    }

    @Test
    void shouldThrowExceptionWhenBuyerCompanyIdIsNull() {
        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> Order.create(
                        null,
                        UUID.randomUUID(),
                        List.of(orderItem(UUID.randomUUID(), "Gaming Mouse", 1, "100"))
                )
        );

        assertEquals("Buyer company id cannot be null.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSellerCompanyIdIsNull() {
        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> Order.create(
                        UUID.randomUUID(),
                        null,
                        List.of(orderItem(UUID.randomUUID(), "Gaming Mouse", 1, "100"))
                )
        );

        assertEquals("Seller company id cannot be null.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenBuyerAndSellerAreSame() {
        UUID companyId = UUID.randomUUID();

        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> Order.create(
                        companyId,
                        companyId,
                        List.of(orderItem(UUID.randomUUID(), "Gaming Mouse", 1, "100"))
                )
        );

        assertEquals("Buyer company and seller company cannot be same.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenItemsAreEmpty() {
        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> Order.create(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        List.of()
                )
        );

        assertEquals("Order must contain at least one item.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenItemIsNull() {
        List<OrderItem> items = new ArrayList<>();
        items.add(null);

        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> Order.create(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        items
                )
        );

        assertEquals("Order item cannot be null.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenOrderContainsDuplicateProductItems() {
        UUID productId = UUID.randomUUID();

        OrderItem firstItem = orderItem(productId, "Gaming Mouse", 1, "100");
        OrderItem secondItem = orderItem(productId, "Gaming Mouse", 2, "100");

        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> Order.create(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        List.of(firstItem, secondItem)
                )
        );

        assertEquals("Order cannot contain duplicate product items.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenOrderItemsHaveDifferentCurrencies() {
        OrderItem firstItem = OrderItem.create(
                UUID.randomUUID(),
                "Gaming Mouse",
                OrderQuantity.of(1),
                Money.of(new BigDecimal("100"), "TRY")
        );

        OrderItem secondItem = OrderItem.create(
                UUID.randomUUID(),
                "Keyboard",
                OrderQuantity.of(1),
                Money.of(new BigDecimal("100"), "USD")
        );

        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> Order.create(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        List.of(firstItem, secondItem)
                )
        );

        assertEquals("All order items must have same currency.", exception.getMessage());
    }

    @Test
    void shouldConfirmPendingOrder() {
        Order order = validOrder();

        order.confirm();

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        assertNotNull(order.getConfirmedAt());
        assertNotNull(order.getUpdatedAt());
    }

    @Test
    void shouldCancelPendingOrder() {
        Order order = validOrder();

        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertNotNull(order.getCancelledAt());
        assertNotNull(order.getUpdatedAt());
    }

    @Test
    void shouldCompleteConfirmedOrder() {
        Order order = validOrder();

        order.confirm();
        order.complete();

        assertEquals(OrderStatus.COMPLETED, order.getStatus());
        assertNotNull(order.getCompletedAt());
        assertNotNull(order.getUpdatedAt());
    }

    @Test
    void shouldThrowExceptionWhenConfirmingNonPendingOrder() {
        Order order = validOrder();
        order.cancel();

        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                order::confirm
        );

        assertEquals("Only pending orders can be confirmed.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCancellingNonPendingOrder() {
        Order order = validOrder();
        order.confirm();

        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                order::cancel
        );

        assertEquals("Only pending orders can be cancelled.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCompletingNonConfirmedOrder() {
        Order order = validOrder();

        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                order::complete
        );

        assertEquals("Only confirmed orders can be completed.", exception.getMessage());
    }

    @Test
    void shouldRestoreOrderWhenValuesAreValid() {
        UUID orderId = UUID.randomUUID();
        UUID buyerCompanyId = UUID.randomUUID();
        UUID sellerCompanyId = UUID.randomUUID();
        OrderItem item = orderItem(UUID.randomUUID(), "Gaming Mouse", 2, "750");

        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        Order order = Order.restore(
                orderId,
                buyerCompanyId,
                sellerCompanyId,
                List.of(item),
                OrderStatus.PENDING,
                Money.of(new BigDecimal("1500"), "TRY"),
                createdAt,
                updatedAt,
                null,
                null,
                null
        );

        assertEquals(orderId, order.getId());
        assertEquals(buyerCompanyId, order.getBuyerCompanyId());
        assertEquals(sellerCompanyId, order.getSellerCompanyId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(Money.of(new BigDecimal("1500"), "TRY"), order.getTotalAmount());
    }

    @Test
    void shouldThrowExceptionWhenRestoredTotalAmountIsInvalid() {
        OrderItem item = orderItem(UUID.randomUUID(), "Gaming Mouse", 2, "750");

        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> Order.restore(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        List.of(item),
                        OrderStatus.PENDING,
                        Money.of(new BigDecimal("1400"), "TRY"),
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now(),
                        null,
                        null,
                        null
                )
        );

        assertEquals("Order total amount is invalid.", exception.getMessage());
    }

    private Order validOrder() {
        return Order.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(orderItem(UUID.randomUUID(), "Gaming Mouse", 2, "750"))
        );
    }

    private OrderItem orderItem(
            UUID productId,
            String productName,
            long quantity,
            String unitPrice
    ) {
        return OrderItem.create(
                productId,
                productName,
                OrderQuantity.of(quantity),
                Money.of(new BigDecimal(unitPrice), "TRY")
        );
    }
}