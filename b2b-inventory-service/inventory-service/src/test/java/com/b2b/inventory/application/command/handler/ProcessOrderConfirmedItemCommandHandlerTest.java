package com.b2b.inventory.application.command.handler;

import com.b2b.inventory.application.command.dto.DecreaseStockCommand;
import com.b2b.inventory.application.command.dto.ProcessOrderConfirmedItemCommand;
import com.b2b.inventory.application.port.in.DecreaseStockUseCase;
import com.b2b.inventory.application.port.out.ProcessedOrderEventRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProcessOrderConfirmedItemCommandHandlerTest {

    private ProcessedOrderEventRepositoryPort processedOrderEventRepositoryPort;
    private DecreaseStockUseCase decreaseStockUseCase;
    private ProcessOrderConfirmedItemCommandHandler handler;

    @BeforeEach
    void setUp() {
        processedOrderEventRepositoryPort = mock(ProcessedOrderEventRepositoryPort.class);
        decreaseStockUseCase = mock(DecreaseStockUseCase.class);

        handler = new ProcessOrderConfirmedItemCommandHandler(
                processedOrderEventRepositoryPort,
                decreaseStockUseCase
        );
    }

    @Test
    void shouldDecreaseStockWhenOrderConfirmedItemEventIsNotProcessedBefore() {
        UUID orderId = UUID.randomUUID();
        UUID sellerCompanyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        ProcessOrderConfirmedItemCommand command = new ProcessOrderConfirmedItemCommand(
                orderId,
                sellerCompanyId,
                productId,
                BigDecimal.valueOf(2)
        );

        when(processedOrderEventRepositoryPort.markAsProcessed(
                "ORDER_CONFIRMED",
                orderId,
                productId
        )).thenReturn(true);

        handler.handle(command);

        ArgumentCaptor<DecreaseStockCommand> decreaseStockCommandCaptor =
                ArgumentCaptor.forClass(DecreaseStockCommand.class);

        verify(decreaseStockUseCase).handle(decreaseStockCommandCaptor.capture());

        DecreaseStockCommand decreaseStockCommand = decreaseStockCommandCaptor.getValue();

        assertEquals(sellerCompanyId, decreaseStockCommand.companyId());
        assertEquals(productId, decreaseStockCommand.productId());
        assertEquals(BigDecimal.valueOf(2), decreaseStockCommand.quantity());
        assertEquals("ORDER_CONFIRMED: " + orderId, decreaseStockCommand.reason());

        verify(processedOrderEventRepositoryPort).markAsProcessed(
                "ORDER_CONFIRMED",
                orderId,
                productId
        );
    }

    @Test
    void shouldIgnoreEventWhenOrderConfirmedItemEventIsProcessedBefore() {
        UUID orderId = UUID.randomUUID();
        UUID sellerCompanyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        ProcessOrderConfirmedItemCommand command = new ProcessOrderConfirmedItemCommand(
                orderId,
                sellerCompanyId,
                productId,
                BigDecimal.valueOf(2)
        );

        when(processedOrderEventRepositoryPort.markAsProcessed(
                "ORDER_CONFIRMED",
                orderId,
                productId
        )).thenReturn(false);

        handler.handle(command);

        verify(decreaseStockUseCase, never()).handle(any(DecreaseStockCommand.class));

        verify(processedOrderEventRepositoryPort).markAsProcessed(
                "ORDER_CONFIRMED",
                orderId,
                productId
        );
    }
}