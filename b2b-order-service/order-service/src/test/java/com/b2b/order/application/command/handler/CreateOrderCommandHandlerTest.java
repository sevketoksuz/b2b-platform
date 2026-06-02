package com.b2b.order.application.command.handler;

import com.b2b.order.application.command.dto.CreateOrderCommand;
import com.b2b.order.application.command.dto.CreateOrderItemCommand;
import com.b2b.order.application.command.dto.CreateOrderResult;
import com.b2b.order.application.port.out.OrderRepositoryPort;
import com.b2b.order.domain.enumtype.OrderStatus;
import com.b2b.order.domain.exception.OrderDomainException;
import com.b2b.order.domain.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateOrderCommandHandlerTest {

    private OrderRepositoryPort orderRepositoryPort;
    private CreateOrderCommandHandler handler;

    @BeforeEach
    void setUp() {
        orderRepositoryPort = mock(OrderRepositoryPort.class);
        handler = new CreateOrderCommandHandler(orderRepositoryPort);
    }

    @Test
    void shouldCreateOrderWhenCommandIsValid() {
        UUID buyerCompanyId = UUID.randomUUID();
        UUID sellerCompanyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        CreateOrderCommand command = new CreateOrderCommand(
                buyerCompanyId,
                sellerCompanyId,
                List.of(
                        new CreateOrderItemCommand(
                                productId,
                                "Gaming Mouse",
                                BigDecimal.valueOf(2),
                                BigDecimal.valueOf(750),
                                "TRY"
                        )
                )
        );

        when(orderRepositoryPort.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateOrderResult result = handler.handle(command);

        assertNotNull(result.id());
        assertEquals(buyerCompanyId, result.buyerCompanyId());
        assertEquals(sellerCompanyId, result.sellerCompanyId());
        assertEquals(OrderStatus.PENDING, result.status());
        assertEquals(new BigDecimal("1500"), result.totalAmount());
        assertEquals("TRY", result.currency());
        assertNotNull(result.createdAt());
        assertNotNull(result.updatedAt());
        assertEquals(1, result.items().size());

        assertEquals(productId, result.items().getFirst().productId());
        assertEquals("Gaming Mouse", result.items().getFirst().productName());
        assertEquals(new BigDecimal("2"), result.items().getFirst().quantity());
        assertEquals(new BigDecimal("750"), result.items().getFirst().unitPriceAmount());
        assertEquals(new BigDecimal("1500"), result.items().getFirst().lineTotalAmount());

        verify(orderRepositoryPort).save(any(Order.class));
    }

    @Test
    void shouldSaveOrderWithCorrectDomainValues() {
        UUID buyerCompanyId = UUID.randomUUID();
        UUID sellerCompanyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        CreateOrderCommand command = new CreateOrderCommand(
                buyerCompanyId,
                sellerCompanyId,
                List.of(
                        new CreateOrderItemCommand(
                                productId,
                                "Gaming Mouse",
                                BigDecimal.valueOf(2),
                                BigDecimal.valueOf(750),
                                "TRY"
                        )
                )
        );

        when(orderRepositoryPort.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        handler.handle(command);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        verify(orderRepositoryPort).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();

        assertEquals(buyerCompanyId, savedOrder.getBuyerCompanyId());
        assertEquals(sellerCompanyId, savedOrder.getSellerCompanyId());
        assertEquals(OrderStatus.PENDING, savedOrder.getStatus());
        assertEquals(new BigDecimal("1500"), savedOrder.getTotalAmount().getAmount());
        assertEquals("TRY", savedOrder.getTotalAmount().getCurrency());
        assertEquals(1, savedOrder.getItems().size());
        assertEquals(productId, savedOrder.getItems().getFirst().getProductId());
    }

    @Test
    void shouldThrowExceptionWhenItemsAreNull() {
        CreateOrderCommand command = new CreateOrderCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                null
        );

        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> handler.handle(command)
        );

        assertEquals("Order must contain at least one item.", exception.getMessage());

        verify(orderRepositoryPort, never()).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenItemsAreEmpty() {
        CreateOrderCommand command = new CreateOrderCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of()
        );

        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> handler.handle(command)
        );

        assertEquals("Order must contain at least one item.", exception.getMessage());

        verify(orderRepositoryPort, never()).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenBuyerAndSellerAreSame() {
        UUID companyId = UUID.randomUUID();

        CreateOrderCommand command = new CreateOrderCommand(
                companyId,
                companyId,
                List.of(
                        new CreateOrderItemCommand(
                                UUID.randomUUID(),
                                "Gaming Mouse",
                                BigDecimal.valueOf(2),
                                BigDecimal.valueOf(750),
                                "TRY"
                        )
                )
        );

        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> handler.handle(command)
        );

        assertEquals("Buyer company and seller company cannot be same.", exception.getMessage());

        verify(orderRepositoryPort, never()).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenCommandContainsDuplicateProductItems() {
        UUID productId = UUID.randomUUID();

        CreateOrderCommand command = new CreateOrderCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(
                        new CreateOrderItemCommand(
                                productId,
                                "Gaming Mouse",
                                BigDecimal.valueOf(1),
                                BigDecimal.valueOf(750),
                                "TRY"
                        ),
                        new CreateOrderItemCommand(
                                productId,
                                "Gaming Mouse",
                                BigDecimal.valueOf(2),
                                BigDecimal.valueOf(750),
                                "TRY"
                        )
                )
        );

        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> handler.handle(command)
        );

        assertEquals("Order cannot contain duplicate product items.", exception.getMessage());

        verify(orderRepositoryPort, never()).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenCommandContainsDifferentCurrencies() {
        CreateOrderCommand command = new CreateOrderCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(
                        new CreateOrderItemCommand(
                                UUID.randomUUID(),
                                "Gaming Mouse",
                                BigDecimal.valueOf(1),
                                BigDecimal.valueOf(750),
                                "TRY"
                        ),
                        new CreateOrderItemCommand(
                                UUID.randomUUID(),
                                "Keyboard",
                                BigDecimal.valueOf(1),
                                BigDecimal.valueOf(100),
                                "USD"
                        )
                )
        );

        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> handler.handle(command)
        );

        assertEquals("All order items must have same currency.", exception.getMessage());

        verify(orderRepositoryPort, never()).save(any(Order.class));
    }
}