package com.b2b.order.application.command.handler;

import com.b2b.order.application.command.dto.CancelOrderCommand;
import com.b2b.order.application.command.dto.ChangeOrderStatusResult;
import com.b2b.order.application.exception.OrderNotFoundException;
import com.b2b.order.application.port.out.OrderRepositoryPort;
import com.b2b.order.domain.enumtype.OrderStatus;
import com.b2b.order.domain.exception.OrderDomainException;
import com.b2b.order.domain.model.Order;
import com.b2b.order.domain.model.OrderItem;
import com.b2b.order.domain.valueobject.Money;
import com.b2b.order.domain.valueobject.OrderQuantity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CancelOrderCommandHandlerTest {

    private OrderRepositoryPort orderRepositoryPort;
    private CancelOrderCommandHandler handler;

    @BeforeEach
    void setUp() {
        orderRepositoryPort = mock(OrderRepositoryPort.class);
        handler = new CancelOrderCommandHandler(orderRepositoryPort);
    }

    @Test
    void shouldCancelPendingOrder() {
        UUID orderId = UUID.randomUUID();

        Order order = pendingOrder(orderId);

        when(orderRepositoryPort.findById(orderId))
                .thenReturn(Optional.of(order));

        when(orderRepositoryPort.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ChangeOrderStatusResult result = handler.handle(new CancelOrderCommand(orderId));

        assertEquals(orderId, result.id());
        assertEquals(OrderStatus.CANCELLED, result.status());
        assertNotNull(result.cancelledAt());

        verify(orderRepositoryPort).findById(orderId);
        verify(orderRepositoryPort).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();

        when(orderRepositoryPort.findById(orderId))
                .thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> handler.handle(new CancelOrderCommand(orderId))
        );

        assertEquals("Order not found with id: " + orderId, exception.getMessage());

        verify(orderRepositoryPort).findById(orderId);
        verify(orderRepositoryPort, never()).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenOrderIsNotPending() {
        UUID orderId = UUID.randomUUID();

        Order order = confirmedOrder(orderId);

        when(orderRepositoryPort.findById(orderId))
                .thenReturn(Optional.of(order));

        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> handler.handle(new CancelOrderCommand(orderId))
        );

        assertEquals("Only pending orders can be cancelled.", exception.getMessage());

        verify(orderRepositoryPort).findById(orderId);
        verify(orderRepositoryPort, never()).save(any(Order.class));
    }

    private Order pendingOrder(UUID orderId) {
        UUID buyerCompanyId = UUID.randomUUID();
        UUID sellerCompanyId = UUID.randomUUID();

        Order createdOrder = Order.create(
                buyerCompanyId,
                sellerCompanyId,
                List.of(orderItem(UUID.randomUUID()))
        );

        return Order.restore(
                orderId,
                buyerCompanyId,
                sellerCompanyId,
                createdOrder.getItems(),
                OrderStatus.PENDING,
                createdOrder.getTotalAmount(),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1),
                null,
                null,
                null
        );
    }

    private Order confirmedOrder(UUID orderId) {
        UUID buyerCompanyId = UUID.randomUUID();
        UUID sellerCompanyId = UUID.randomUUID();

        Order createdOrder = Order.create(
                buyerCompanyId,
                sellerCompanyId,
                List.of(orderItem(UUID.randomUUID()))
        );

        return Order.restore(
                orderId,
                buyerCompanyId,
                sellerCompanyId,
                createdOrder.getItems(),
                OrderStatus.CONFIRMED,
                createdOrder.getTotalAmount(),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                null
        );
    }

    private OrderItem orderItem(UUID productId) {
        return OrderItem.create(
                productId,
                "Gaming Mouse",
                OrderQuantity.of(2),
                Money.of(new BigDecimal("750"), "TRY")
        );
    }
}