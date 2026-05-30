package com.b2b.inventory.application.query.handler;

import com.b2b.inventory.application.port.out.PagedResult;
import com.b2b.inventory.application.port.out.StockMovementRepositoryPort;
import com.b2b.inventory.application.port.out.StockMovementSearchCriteria;
import com.b2b.inventory.application.query.dto.GetStockMovementsQuery;
import com.b2b.inventory.application.query.dto.GetStockMovementsResult;
import com.b2b.inventory.domain.enumtype.StockMovementType;
import com.b2b.inventory.domain.model.StockMovement;
import com.b2b.inventory.domain.valueobject.Quantity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetStockMovementsQueryHandlerTest {

    private StockMovementRepositoryPort stockMovementRepositoryPort;
    private GetStockMovementsQueryHandler handler;

    @BeforeEach
    void setUp() {
        stockMovementRepositoryPort = mock(StockMovementRepositoryPort.class);
        handler = new GetStockMovementsQueryHandler(stockMovementRepositoryPort);
    }

    @Test
    void shouldReturnPagedStockMovementsWhenQueryIsValid() {
        UUID companyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        LocalDateTime fromDate = LocalDateTime.now().minusDays(7);
        LocalDateTime toDate = LocalDateTime.now();

        StockMovement movement = StockMovement.restore(
                UUID.randomUUID(),
                companyId,
                productId,
                StockMovementType.INCREASE,
                Quantity.of(10),
                Quantity.of(5),
                Quantity.of(15),
                "Initial stock",
                LocalDateTime.now().minusDays(1)
        );

        GetStockMovementsQuery query = new GetStockMovementsQuery(
                0,
                10,
                companyId,
                productId,
                StockMovementType.INCREASE,
                fromDate,
                toDate
        );

        when(stockMovementRepositoryPort.findStockMovements(any(StockMovementSearchCriteria.class)))
                .thenReturn(new PagedResult<>(
                        List.of(movement),
                        0,
                        10,
                        1,
                        1
                ));

        GetStockMovementsResult result = handler.handle(query);

        assertEquals(1, result.content().size());
        assertEquals(0, result.page());
        assertEquals(10, result.size());
        assertEquals(1, result.totalElements());
        assertEquals(1, result.totalPages());

        assertEquals(companyId, result.content().get(0).companyId());
        assertEquals(productId, result.content().get(0).productId());
        assertEquals(StockMovementType.INCREASE, result.content().get(0).movementType());
        assertEquals(new BigDecimal("10"), result.content().get(0).quantity());
        assertEquals(new BigDecimal("5"), result.content().get(0).previousQuantity());
        assertEquals(new BigDecimal("15"), result.content().get(0).newQuantity());
        assertEquals("Initial stock", result.content().get(0).reason());

        ArgumentCaptor<StockMovementSearchCriteria> criteriaCaptor =
                ArgumentCaptor.forClass(StockMovementSearchCriteria.class);

        verify(stockMovementRepositoryPort).findStockMovements(criteriaCaptor.capture());

        StockMovementSearchCriteria criteria = criteriaCaptor.getValue();

        assertEquals(0, criteria.page());
        assertEquals(10, criteria.size());
        assertEquals(companyId, criteria.companyId());
        assertEquals(productId, criteria.productId());
        assertEquals(StockMovementType.INCREASE, criteria.movementType());
        assertEquals(fromDate, criteria.fromDate());
        assertEquals(toDate, criteria.toDate());
    }

    @Test
    void shouldThrowExceptionWhenPageIsNegative() {
        GetStockMovementsQuery query = new GetStockMovementsQuery(
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

        verify(stockMovementRepositoryPort, never())
                .findStockMovements(any(StockMovementSearchCriteria.class));
    }

    @Test
    void shouldThrowExceptionWhenSizeIsLessThanOne() {
        GetStockMovementsQuery query = new GetStockMovementsQuery(
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

        verify(stockMovementRepositoryPort, never())
                .findStockMovements(any(StockMovementSearchCriteria.class));
    }

    @Test
    void shouldThrowExceptionWhenSizeIsGreaterThanOneHundred() {
        GetStockMovementsQuery query = new GetStockMovementsQuery(
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

        verify(stockMovementRepositoryPort, never())
                .findStockMovements(any(StockMovementSearchCriteria.class));
    }

    @Test
    void shouldThrowExceptionWhenFromDateIsAfterToDate() {
        LocalDateTime fromDate = LocalDateTime.now();
        LocalDateTime toDate = LocalDateTime.now().minusDays(1);

        GetStockMovementsQuery query = new GetStockMovementsQuery(
                0,
                10,
                null,
                null,
                null,
                fromDate,
                toDate
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> handler.handle(query)
        );

        assertEquals("From date cannot be after to date.", exception.getMessage());

        verify(stockMovementRepositoryPort, never())
                .findStockMovements(any(StockMovementSearchCriteria.class));
    }
}