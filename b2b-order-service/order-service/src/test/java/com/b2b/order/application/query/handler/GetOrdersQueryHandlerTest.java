package com.b2b.order.application.query.handler;

import com.b2b.order.application.port.out.OrderRepositoryPort;
import com.b2b.order.application.port.out.OrderSearchCriteria;
import com.b2b.order.application.port.out.PagedResult;
import com.b2b.order.application.query.dto.GetOrdersQuery;
import com.b2b.order.application.query.dto.GetOrdersResult;
import com.b2b.order.domain.enumtype.OrderStatus;
import com.b2b.order.domain.model.Order;
import com.b2b.order.domain.model.OrderItem;
import com.b2b.order.domain.valueobject.Money;
import com.b2b.order.domain.valueobject.OrderQuantity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetOrdersQueryHandlerTest {

    private OrderRepositoryPort orderRepositoryPort;
    private GetOrdersQueryHandler handler;

    @BeforeEach
    void setUp() {
        orderRepositoryPort = mock(OrderRepositoryPort.class);
        handler = new GetOrdersQueryHandler(orderRepositoryPort);
    }

    @Test
    void shouldReturnPagedOrders() {
        UUID buyerCompanyId = UUID.randomUUID();
        UUID sellerCompanyId = UUID.randomUUID();

        Order firstOrder = order(buyerCompanyId, sellerCompanyId);
        Order secondOrder = order(buyerCompanyId, sellerCompanyId);

        GetOrdersQuery query = new GetOrdersQuery(
                0,
                10,
                buyerCompanyId,
                sellerCompanyId,
                OrderStatus.PENDING,
                null,
                null
        );

        when(orderRepositoryPort.findOrders(any(OrderSearchCriteria.class)))
                .thenReturn(new PagedResult<>(
                        List.of(firstOrder, secondOrder),
                        0,
                        10,
                        2,
                        1
                ));

        GetOrdersResult result = handler.handle(query);

        assertEquals(2, result.content().size());
        assertEquals(0, result.page());
        assertEquals(10, result.size());
        assertEquals(2, result.totalElements());
        assertEquals(1, result.totalPages());

        assertEquals(OrderStatus.PENDING, result.content().getFirst().status());
        assertEquals(new BigDecimal("1500"), result.content().getFirst().totalAmount());
        assertEquals("TRY", result.content().getFirst().currency());

        verify(orderRepositoryPort).findOrders(any(OrderSearchCriteria.class));
    }

    @Test
    void shouldPassSearchCriteriaToRepository() {
        UUID buyerCompanyId = UUID.randomUUID();
        UUID sellerCompanyId = UUID.randomUUID();
        LocalDateTime fromDate = LocalDateTime.now().minusDays(7);
        LocalDateTime toDate = LocalDateTime.now();

        GetOrdersQuery query = new GetOrdersQuery(
                1,
                20,
                buyerCompanyId,
                sellerCompanyId,
                OrderStatus.CONFIRMED,
                fromDate,
                toDate
        );

        when(orderRepositoryPort.findOrders(any(OrderSearchCriteria.class)))
                .thenReturn(new PagedResult<>(
                        List.of(),
                        1,
                        20,
                        0,
                        0
                ));

        handler.handle(query);

        ArgumentCaptor<OrderSearchCriteria> criteriaCaptor =
                ArgumentCaptor.forClass(OrderSearchCriteria.class);

        verify(orderRepositoryPort).findOrders(criteriaCaptor.capture());

        OrderSearchCriteria criteria = criteriaCaptor.getValue();

        assertEquals(1, criteria.page());
        assertEquals(20, criteria.size());
        assertEquals(buyerCompanyId, criteria.buyerCompanyId());
        assertEquals(sellerCompanyId, criteria.sellerCompanyId());
        assertEquals(OrderStatus.CONFIRMED, criteria.status());
        assertEquals(fromDate, criteria.fromDate());
        assertEquals(toDate, criteria.toDate());
    }

    @Test
    void shouldThrowExceptionWhenPageIsNegative() {
        GetOrdersQuery query = new GetOrdersQuery(
                -1,
                10,
                null,
                null,
                null,
                null,
                null
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> handler.handle(query)
        );

        assertEquals("Page cannot be negative.", exception.getMessage());

        verify(orderRepositoryPort, never()).findOrders(any(OrderSearchCriteria.class));
    }

    @Test
    void shouldThrowExceptionWhenSizeIsLessThanOne() {
        GetOrdersQuery query = new GetOrdersQuery(
                0,
                0,
                null,
                null,
                null,
                null,
                null
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> handler.handle(query)
        );

        assertEquals("Size must be between 1 and 100.", exception.getMessage());

        verify(orderRepositoryPort, never()).findOrders(any(OrderSearchCriteria.class));
    }

    @Test
    void shouldThrowExceptionWhenSizeIsGreaterThanHundred() {
        GetOrdersQuery query = new GetOrdersQuery(
                0,
                101,
                null,
                null,
                null,
                null,
                null
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> handler.handle(query)
        );

        assertEquals("Size must be between 1 and 100.", exception.getMessage());

        verify(orderRepositoryPort, never()).findOrders(any(OrderSearchCriteria.class));
    }

    private Order order(UUID buyerCompanyId, UUID sellerCompanyId) {
        return Order.create(
                buyerCompanyId,
                sellerCompanyId,
                List.of(orderItem(UUID.randomUUID()))
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