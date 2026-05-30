package com.b2b.inventory.application.query.handler;

import com.b2b.inventory.application.exception.StockNotFoundException;
import com.b2b.inventory.application.port.out.StockRepositoryPort;
import com.b2b.inventory.application.query.dto.GetStockByProductIdQuery;
import com.b2b.inventory.application.query.dto.GetStockByProductIdResult;
import com.b2b.inventory.domain.model.Stock;
import com.b2b.inventory.domain.valueobject.Quantity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetStockByProductIdQueryHandlerTest {

    private StockRepositoryPort stockRepositoryPort;
    private GetStockByProductIdQueryHandler handler;

    @BeforeEach
    void setUp() {
        stockRepositoryPort = mock(StockRepositoryPort.class);
        handler = new GetStockByProductIdQueryHandler(stockRepositoryPort);
    }

    @Test
    void shouldReturnStockWhenStockExistsForProduct() {
        UUID stockId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Stock stock = Stock.restore(
                stockId,
                companyId,
                productId,
                Quantity.of(25),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );

        GetStockByProductIdQuery query = new GetStockByProductIdQuery(productId);

        when(stockRepositoryPort.findByProductId(productId))
                .thenReturn(Optional.of(stock));

        GetStockByProductIdResult result = handler.handle(query);

        assertEquals(stockId, result.id());
        assertEquals(companyId, result.companyId());
        assertEquals(productId, result.productId());
        assertEquals(new BigDecimal("25"), result.quantity());
        assertNotNull(result.createdAt());
        assertNotNull(result.updatedAt());

        verify(stockRepositoryPort).findByProductId(productId);
    }

    @Test
    void shouldThrowExceptionWhenStockNotFoundForProduct() {
        UUID productId = UUID.randomUUID();

        GetStockByProductIdQuery query = new GetStockByProductIdQuery(productId);

        when(stockRepositoryPort.findByProductId(productId))
                .thenReturn(Optional.empty());

        StockNotFoundException exception = assertThrows(
                StockNotFoundException.class,
                () -> handler.handle(query)
        );

        assertEquals("Stock not found for product id: " + productId, exception.getMessage());

        verify(stockRepositoryPort).findByProductId(productId);
    }
}