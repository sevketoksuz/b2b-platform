package com.b2b.order.application.command.handler;

import com.b2b.order.application.command.dto.ChangeOrderStatusResult;
import com.b2b.order.application.command.dto.ConfirmOrderCommand;
import com.b2b.order.application.exception.InventoryClientException;
import com.b2b.order.application.exception.OrderNotFoundException;
import com.b2b.order.application.port.out.InventoryClientPort;
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

class ConfirmOrderCommandHandlerTest {

    private OrderRepositoryPort orderRepositoryPort;
    private InventoryClientPort inventoryClientPort;
    private ConfirmOrderCommandHandler handler;

    @BeforeEach
    void setUp() {
        orderRepositoryPort = mock(OrderRepositoryPort.class);
        inventoryClientPort = mock(InventoryClientPort.class);

        handler = new ConfirmOrderCommandHandler(
                orderRepositoryPort,
                inventoryClientPort
        );
    }

    @Test
    void shouldConfirmOrderAndDecreaseStockWhenOrderIsPending() {
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
        verify(inventoryClientPort).decreaseStock(
                sellerCompanyId,
                productId,
                new BigDecimal("2"),
                "Order confirmed: " + orderId
        );
        verify(orderRepositoryPort).save(any(Order.class));
    }

    @Test
    void shouldDecreaseStockForEachOrderItem() {
        UUID orderId = UUID.randomUUID();
        UUID buyerCompanyId = UUID.randomUUID();
        UUID sellerCompanyId = UUID.randomUUID();
        UUID firstProductId = UUID.randomUUID();
        UUID secondProductId = UUID.randomUUID();

        Order order = Order.create(
                buyerCompanyId,
                sellerCompanyId,
                List.of(
                        orderItem(firstProductId, "Gaming Mouse", 2, "750"),
                        orderItem(secondProductId, "Keyboard", 1, "1000")
                )
        );

        Order restoredOrder = Order.restore(
                orderId,
                buyerCompanyId,
                sellerCompanyId,
                order.getItems(),
                OrderStatus.PENDING,
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                null,
                null,
                null
        );

        when(orderRepositoryPort.findById(orderId))
                .thenReturn(Optional.of(restoredOrder));

        when(orderRepositoryPort.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        handler.handle(new ConfirmOrderCommand(orderId));

        verify(inventoryClientPort).decreaseStock(
                sellerCompanyId,
                firstProductId,
                new BigDecimal("2"),
                "Order confirmed: " + orderId
        );

        verify(inventoryClientPort).decreaseStock(
                sellerCompanyId,
                secondProductId,
                new BigDecimal("1"),
                "Order confirmed: " + orderId
        );

        verify(inventoryClientPort, times(2))
                .decreaseStock(any(), any(), any(), any());
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
        verify(inventoryClientPort, never()).decreaseStock(any(), any(), any(), any());
        verify(orderRepositoryPort, never()).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionAndNotDecreaseStockWhenOrderIsNotPending() {
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
        verify(inventoryClientPort, never()).decreaseStock(any(), any(), any(), any());
        verify(orderRepositoryPort, never()).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionAndNotSaveOrderWhenInventoryClientFails() {
        UUID orderId = UUID.randomUUID();
        UUID buyerCompanyId = UUID.randomUUID();
        UUID sellerCompanyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Order order = pendingOrder(orderId, buyerCompanyId, sellerCompanyId, productId);

        when(orderRepositoryPort.findById(orderId))
                .thenReturn(Optional.of(order));

        doThrow(new InventoryClientException("Inventory Service stock decrease request failed."))
                .when(inventoryClientPort)
                .decreaseStock(any(), any(), any(), any());

        InventoryClientException exception = assertThrows(
                InventoryClientException.class,
                () -> handler.handle(new ConfirmOrderCommand(orderId))
        );

        assertEquals("Inventory Service stock decrease request failed.", exception.getMessage());

        verify(orderRepositoryPort).findById(orderId);
        verify(inventoryClientPort).decreaseStock(
                sellerCompanyId,
                productId,
                new BigDecimal("2"),
                "Order confirmed: " + orderId
        );
        verify(orderRepositoryPort, never()).save(any(Order.class));
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