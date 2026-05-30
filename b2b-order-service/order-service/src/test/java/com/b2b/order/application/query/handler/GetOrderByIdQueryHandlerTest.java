package com.b2b.order.application.query.handler;

import com.b2b.order.application.exception.OrderNotFoundException;
import com.b2b.order.application.port.out.OrderRepositoryPort;
import com.b2b.order.application.query.dto.GetOrderByIdQuery;
import com.b2b.order.application.query.dto.GetOrderByIdResult;
import com.b2b.order.domain.enumtype.OrderStatus;
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

class GetOrderByIdQueryHandlerTest {

    private OrderRepositoryPort orderRepositoryPort;
    private GetOrderByIdQueryHandler handler;

    @BeforeEach
    void setUp() {
        orderRepositoryPort = mock(OrderRepositoryPort.class);
        handler = new GetOrderByIdQueryHandler(orderRepositoryPort);
    }

    @Test
    void shouldReturnOrderWhenOrderExists() {
        UUID orderId = UUID.randomUUID();
        UUID buyerCompanyId = UUID.randomUUID();
        UUID sellerCompanyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Order order = pendingOrder(orderId, buyerCompanyId, sellerCompanyId, productId);

        when(orderRepositoryPort.findById(orderId))
                .thenReturn(Optional.of(order));

        GetOrderByIdResult result = handler.handle(new GetOrderByIdQuery(orderId));

        assertEquals(orderId, result.id());
        assertEquals(buyerCompanyId, result.buyerCompanyId());
        assertEquals(sellerCompanyId, result.sellerCompanyId());
        assertEquals(OrderStatus.PENDING, result.status());
        assertEquals(new BigDecimal("1500"), result.totalAmount());
        assertEquals("TRY", result.currency());
        assertEquals(1, result.items().size());
        assertEquals(productId, result.items().getFirst().productId());

        verify(orderRepositoryPort).findById(orderId);
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();

        when(orderRepositoryPort.findById(orderId))
                .thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> handler.handle(new GetOrderByIdQuery(orderId))
        );

        assertEquals("Order not found with id: " + orderId, exception.getMessage());

        verify(orderRepositoryPort).findById(orderId);
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
                List.of(orderItem(productId))
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

    private OrderItem orderItem(UUID productId) {
        return OrderItem.create(
                productId,
                "Gaming Mouse",
                OrderQuantity.of(2),
                Money.of(new BigDecimal("750"), "TRY")
        );
    }
}