package com.b2b.order.application.command.handler;

import com.b2b.order.application.command.dto.ChangeOrderStatusResult;
import com.b2b.order.application.command.dto.ConfirmOrderCommand;
import com.b2b.order.application.exception.OrderNotFoundException;
import com.b2b.order.application.port.out.OrderEventPublisherPort;
import com.b2b.order.application.port.out.OrderRepositoryPort;
import com.b2b.order.domain.enumtype.OrderStatus;
import com.b2b.order.domain.exception.OrderDomainException;
import com.b2b.order.domain.model.Order;
import com.b2b.order.domain.model.OrderItem;
import com.b2b.order.domain.valueobject.Money;
import com.b2b.order.domain.valueobject.OrderQuantity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConfirmOrderCommandHandlerTest {

    private OrderRepositoryPort orderRepositoryPort;
    private OrderEventPublisherPort orderEventPublisherPort;
    private ConfirmOrderCommandHandler handler;

    @BeforeEach
    void setUp() {
        orderRepositoryPort = mock(OrderRepositoryPort.class);
        orderEventPublisherPort = mock(OrderEventPublisherPort.class);

        handler = new ConfirmOrderCommandHandler(
                orderRepositoryPort,
                orderEventPublisherPort
        );
    }

    @Test
    void shouldConfirmOrderAndPublishOrderConfirmedEventWhenOrderIsPending() {
        UUID orderId = UUID.randomUUID();
        UUID buyerCompanyId = UUID.randomUUID();
        UUID sellerCompanyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Order order = pendingOrder(orderId, buyerCompanyId, sellerCompanyId, productId);

        when(orderRepositoryPort.findById(orderId))
                .thenReturn(Optional.of(order));

        when(orderRepositoryPort.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ChangeOrderStatusResult result = handler.handle(new ConfirmOrderCommand(orderId));

        assertEquals(orderId, result.id());
        assertEquals(buyerCompanyId, result.buyerCompanyId());
        assertEquals(sellerCompanyId, result.sellerCompanyId());
        assertEquals(OrderStatus.CONFIRMED, result.status());
        assertEquals(new BigDecimal("1500"), result.totalAmount());
        assertEquals("TRY", result.currency());
        assertNotNull(result.confirmedAt());

        verify(orderRepositoryPort).findById(orderId);
        verify(orderRepositoryPort).save(any(Order.class));

        ArgumentCaptor<Order> eventOrderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderEventPublisherPort).publishOrderConfirmed(eventOrderCaptor.capture());

        Order publishedOrder = eventOrderCaptor.getValue();

        assertEquals(orderId, publishedOrder.getId());
        assertEquals(buyerCompanyId, publishedOrder.getBuyerCompanyId());
        assertEquals(sellerCompanyId, publishedOrder.getSellerCompanyId());
        assertEquals(OrderStatus.CONFIRMED, publishedOrder.getStatus());
        assertNotNull(publishedOrder.getConfirmedAt());
    }

    @Test
    void shouldSaveOrderBeforePublishingOrderConfirmedEvent() {
        UUID orderId = UUID.randomUUID();
        UUID buyerCompanyId = UUID.randomUUID();
        UUID sellerCompanyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Order order = pendingOrder(orderId, buyerCompanyId, sellerCompanyId, productId);

        when(orderRepositoryPort.findById(orderId))
                .thenReturn(Optional.of(order));

        when(orderRepositoryPort.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        handler.handle(new ConfirmOrderCommand(orderId));

        InOrder inOrder = inOrder(orderRepositoryPort, orderEventPublisherPort);

        inOrder.verify(orderRepositoryPort).findById(orderId);
        inOrder.verify(orderRepositoryPort).save(any(Order.class));
        inOrder.verify(orderEventPublisherPort).publishOrderConfirmed(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();

        when(orderRepositoryPort.findById(orderId))
                .thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> handler.handle(new ConfirmOrderCommand(orderId))
        );

        assertEquals("Order not found with id: " + orderId, exception.getMessage());

        verify(orderRepositoryPort).findById(orderId);
        verify(orderRepositoryPort, never()).save(any(Order.class));
        verify(orderEventPublisherPort, never()).publishOrderConfirmed(any(Order.class));
    }

    @Test
    void shouldThrowExceptionAndNotPublishEventWhenOrderIsNotPending() {
        UUID orderId = UUID.randomUUID();
        UUID buyerCompanyId = UUID.randomUUID();
        UUID sellerCompanyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Order order = cancelledOrder(orderId, buyerCompanyId, sellerCompanyId, productId);

        when(orderRepositoryPort.findById(orderId))
                .thenReturn(Optional.of(order));

        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> handler.handle(new ConfirmOrderCommand(orderId))
        );

        assertEquals("Only pending orders can be confirmed.", exception.getMessage());

        verify(orderRepositoryPort).findById(orderId);
        verify(orderRepositoryPort, never()).save(any(Order.class));
        verify(orderEventPublisherPort, never()).publishOrderConfirmed(any(Order.class));
    }

    private Order pendingOrder(
            UUID orderId,
            UUID buyerCompanyId,
            UUID sellerCompanyId,
            UUID productId
    ) {
        Order createdOrder = Order.create(
                buyerCompanyId,
                sellerCompanyId,
                List.of(orderItem(productId, "Gaming Mouse", 2, "750"))
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

    private Order cancelledOrder(
            UUID orderId,
            UUID buyerCompanyId,
            UUID sellerCompanyId,
            UUID productId
    ) {
        Order createdOrder = Order.create(
                buyerCompanyId,
                sellerCompanyId,
                List.of(orderItem(productId, "Gaming Mouse", 2, "750"))
        );

        return Order.restore(
                orderId,
                buyerCompanyId,
                sellerCompanyId,
                createdOrder.getItems(),
                OrderStatus.CANCELLED,
                createdOrder.getTotalAmount(),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(),
                null,
                LocalDateTime.now(),
                null
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