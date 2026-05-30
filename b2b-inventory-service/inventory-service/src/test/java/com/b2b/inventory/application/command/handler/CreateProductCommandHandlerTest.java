package com.b2b.inventory.application.command.handler;

import com.b2b.inventory.application.command.dto.CreateProductCommand;
import com.b2b.inventory.application.command.dto.CreateProductResult;
import com.b2b.inventory.application.exception.ProductAlreadyExistsException;
import com.b2b.inventory.application.port.out.ProductRepositoryPort;
import com.b2b.inventory.application.port.out.StockRepositoryPort;
import com.b2b.inventory.domain.enumtype.ProductStatus;
import com.b2b.inventory.domain.enumtype.ProductUnit;
import com.b2b.inventory.domain.model.Product;
import com.b2b.inventory.domain.model.Stock;
import com.b2b.inventory.domain.valueobject.Quantity;
import com.b2b.inventory.domain.valueobject.Sku;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CreateProductCommandHandlerTest {

    private ProductRepositoryPort productRepositoryPort;
    private StockRepositoryPort stockRepositoryPort;
    private CreateProductCommandHandler handler;

    @BeforeEach
    void setUp() {
        productRepositoryPort = mock(ProductRepositoryPort.class);
        stockRepositoryPort = mock(StockRepositoryPort.class);

        handler = new CreateProductCommandHandler(
                productRepositoryPort,
                stockRepositoryPort
        );
    }

    @Test
    void shouldCreateProductAndInitialStockWhenSkuIsUnique() {
        UUID companyId = UUID.randomUUID();

        CreateProductCommand command = new CreateProductCommand(
                companyId,
                "laptop-001",
                "Gaming Laptop",
                "High performance laptop",
                ProductUnit.PIECE
        );

        when(productRepositoryPort.existsByCompanyIdAndSku(eq(companyId), any(Sku.class)))
                .thenReturn(false);

        when(productRepositoryPort.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(stockRepositoryPort.save(any(Stock.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateProductResult result = handler.handle(command);

        assertNotNull(result.id());
        assertEquals(companyId, result.companyId());
        assertEquals("LAPTOP-001", result.sku());
        assertEquals("Gaming Laptop", result.name());
        assertEquals("High performance laptop", result.description());
        assertEquals(ProductUnit.PIECE, result.unit());
        assertEquals(ProductStatus.ACTIVE, result.status());
        assertNotNull(result.createdAt());

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepositoryPort).save(productCaptor.capture());

        Product savedProductArgument = productCaptor.getValue();

        ArgumentCaptor<Stock> stockCaptor = ArgumentCaptor.forClass(Stock.class);
        verify(stockRepositoryPort).save(stockCaptor.capture());

        Stock savedStockArgument = stockCaptor.getValue();

        assertEquals(companyId, savedStockArgument.getCompanyId());
        assertEquals(savedProductArgument.getId(), savedStockArgument.getProductId());
        assertEquals(Quantity.zero(), savedStockArgument.getQuantity());

        verify(productRepositoryPort).existsByCompanyIdAndSku(eq(companyId), any(Sku.class));
    }

    @Test
    void shouldThrowExceptionWhenProductSkuAlreadyExistsInSameCompany() {
        UUID companyId = UUID.randomUUID();

        CreateProductCommand command = new CreateProductCommand(
                companyId,
                "laptop-001",
                "Gaming Laptop",
                "High performance laptop",
                ProductUnit.PIECE
        );

        when(productRepositoryPort.existsByCompanyIdAndSku(eq(companyId), any(Sku.class)))
                .thenReturn(true);

        ProductAlreadyExistsException exception = assertThrows(
                ProductAlreadyExistsException.class,
                () -> handler.handle(command)
        );

        assertEquals("Product already exists with SKU: LAPTOP-001", exception.getMessage());

        verify(productRepositoryPort).existsByCompanyIdAndSku(eq(companyId), any(Sku.class));
        verify(productRepositoryPort, never()).save(any(Product.class));
        verify(stockRepositoryPort, never()).save(any(Stock.class));
    }
}