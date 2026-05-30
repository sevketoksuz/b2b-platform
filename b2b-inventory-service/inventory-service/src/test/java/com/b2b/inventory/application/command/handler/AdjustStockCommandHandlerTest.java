package com.b2b.inventory.application.command.handler;

import com.b2b.inventory.application.command.dto.AdjustStockCommand;
import com.b2b.inventory.application.command.dto.ChangeStockResult;
import com.b2b.inventory.application.exception.ProductNotActiveException;
import com.b2b.inventory.application.exception.ProductNotFoundException;
import com.b2b.inventory.application.exception.StockNotFoundException;
import com.b2b.inventory.application.port.out.ProductRepositoryPort;
import com.b2b.inventory.application.port.out.StockMovementRepositoryPort;
import com.b2b.inventory.application.port.out.StockRepositoryPort;
import com.b2b.inventory.domain.enumtype.ProductStatus;
import com.b2b.inventory.domain.enumtype.ProductUnit;
import com.b2b.inventory.domain.enumtype.StockMovementType;
import com.b2b.inventory.domain.exception.StockDomainException;
import com.b2b.inventory.domain.model.Product;
import com.b2b.inventory.domain.model.Stock;
import com.b2b.inventory.domain.model.StockMovement;
import com.b2b.inventory.domain.valueobject.Quantity;
import com.b2b.inventory.domain.valueobject.Sku;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdjustStockCommandHandlerTest {

    private ProductRepositoryPort productRepositoryPort;
    private StockRepositoryPort stockRepositoryPort;
    private StockMovementRepositoryPort stockMovementRepositoryPort;
    private AdjustStockCommandHandler handler;

    @BeforeEach
    void setUp() {
        productRepositoryPort = mock(ProductRepositoryPort.class);
        stockRepositoryPort = mock(StockRepositoryPort.class);
        stockMovementRepositoryPort = mock(StockMovementRepositoryPort.class);

        handler = new AdjustStockCommandHandler(
                productRepositoryPort,
                stockRepositoryPort,
                stockMovementRepositoryPort
        );
    }

    @Test
    void shouldAdjustStockAndCreateMovementWhenProductIsActive() {
        UUID companyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID stockId = UUID.randomUUID();

        Product product = product(productId, companyId, ProductStatus.ACTIVE);

        Stock stock = stock(
                stockId,
                companyId,
                productId,
                Quantity.of(20)
        );

        AdjustStockCommand command = new AdjustStockCommand(
                companyId,
                productId,
                BigDecimal.valueOf(100),
                "Stock count correction"
        );

        when(productRepositoryPort.findById(productId))
                .thenReturn(Optional.of(product));

        when(stockRepositoryPort.findByCompanyIdAndProductId(companyId, productId))
                .thenReturn(Optional.of(stock));

        when(stockRepositoryPort.save(any(Stock.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(stockMovementRepositoryPort.save(any(StockMovement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ChangeStockResult result = handler.handle(command);

        assertEquals(stockId, result.id());
        assertEquals(companyId, result.companyId());
        assertEquals(productId, result.productId());
        assertEquals(new BigDecimal("100"), result.quantity());

        ArgumentCaptor<StockMovement> movementCaptor =
                ArgumentCaptor.forClass(StockMovement.class);

        verify(stockMovementRepositoryPort).save(movementCaptor.capture());

        StockMovement movement = movementCaptor.getValue();

        assertEquals(StockMovementType.ADJUSTMENT, movement.getMovementType());
        assertEquals(Quantity.of(80), movement.getQuantity());
        assertEquals(Quantity.of(20), movement.getPreviousQuantity());
        assertEquals(Quantity.of(100), movement.getNewQuantity());
        assertEquals("Stock count correction", movement.getReason());

        verify(productRepositoryPort).findById(productId);
        verify(stockRepositoryPort).findByCompanyIdAndProductId(companyId, productId);
        verify(stockRepositoryPort).save(any(Stock.class));
    }

    @Test
    void shouldThrowExceptionWhenAdjustingToSameQuantity() {
        UUID companyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID stockId = UUID.randomUUID();

        Product product = product(productId, companyId, ProductStatus.ACTIVE);

        Stock stock = stock(
                stockId,
                companyId,
                productId,
                Quantity.of(20)
        );

        AdjustStockCommand command = new AdjustStockCommand(
                companyId,
                productId,
                BigDecimal.valueOf(20),
                "No change"
        );

        when(productRepositoryPort.findById(productId))
                .thenReturn(Optional.of(product));

        when(stockRepositoryPort.findByCompanyIdAndProductId(companyId, productId))
                .thenReturn(Optional.of(stock));

        StockDomainException exception = assertThrows(
                StockDomainException.class,
                () -> handler.handle(command)
        );

        assertEquals("New quantity is same as current quantity.", exception.getMessage());

        verify(productRepositoryPort).findById(productId);
        verify(stockRepositoryPort).findByCompanyIdAndProductId(companyId, productId);
        verify(stockRepositoryPort, never()).save(any(Stock.class));
        verify(stockMovementRepositoryPort, never()).save(any(StockMovement.class));
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        UUID companyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        AdjustStockCommand command = new AdjustStockCommand(
                companyId,
                productId,
                BigDecimal.valueOf(100),
                "Stock count correction"
        );

        when(productRepositoryPort.findById(productId))
                .thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> handler.handle(command)
        );

        assertEquals("Product not found with id: " + productId, exception.getMessage());

        verify(productRepositoryPort).findById(productId);
        verify(stockRepositoryPort, never()).save(any(Stock.class));
        verify(stockMovementRepositoryPort, never()).save(any(StockMovement.class));
    }

    @Test
    void shouldThrowExceptionWhenProductIsInactive() {
        UUID companyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Product product = product(productId, companyId, ProductStatus.INACTIVE);

        AdjustStockCommand command = new AdjustStockCommand(
                companyId,
                productId,
                BigDecimal.valueOf(100),
                "Stock count correction"
        );

        when(productRepositoryPort.findById(productId))
                .thenReturn(Optional.of(product));

        ProductNotActiveException exception = assertThrows(
                ProductNotActiveException.class,
                () -> handler.handle(command)
        );

        assertEquals(
                "Cannot change stock for inactive product: " + productId,
                exception.getMessage()
        );

        verify(productRepositoryPort).findById(productId);
        verify(stockRepositoryPort, never()).save(any(Stock.class));
        verify(stockMovementRepositoryPort, never()).save(any(StockMovement.class));
    }

    @Test
    void shouldThrowExceptionWhenStockNotFound() {
        UUID companyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Product product = product(productId, companyId, ProductStatus.ACTIVE);

        AdjustStockCommand command = new AdjustStockCommand(
                companyId,
                productId,
                BigDecimal.valueOf(100),
                "Stock count correction"
        );

        when(productRepositoryPort.findById(productId))
                .thenReturn(Optional.of(product));

        when(stockRepositoryPort.findByCompanyIdAndProductId(companyId, productId))
                .thenReturn(Optional.empty());

        StockNotFoundException exception = assertThrows(
                StockNotFoundException.class,
                () -> handler.handle(command)
        );

        assertEquals("Stock not found for product id: " + productId, exception.getMessage());

        verify(productRepositoryPort).findById(productId);
        verify(stockRepositoryPort).findByCompanyIdAndProductId(companyId, productId);
        verify(stockRepositoryPort, never()).save(any(Stock.class));
        verify(stockMovementRepositoryPort, never()).save(any(StockMovement.class));
    }

    private Product product(UUID productId, UUID companyId, ProductStatus status) {
        return Product.restore(
                productId,
                companyId,
                Sku.of("MOUSE-001"),
                "Gaming Mouse",
                "Wireless gaming mouse",
                ProductUnit.PIECE,
                status,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );
    }

    private Stock stock(
            UUID stockId,
            UUID companyId,
            UUID productId,
            Quantity quantity
    ) {
        return Stock.restore(
                stockId,
                companyId,
                productId,
                quantity,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );
    }
}