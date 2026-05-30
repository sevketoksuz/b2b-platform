package com.b2b.inventory.application.command.handler;

import com.b2b.inventory.application.command.dto.UpdateProductCommand;
import com.b2b.inventory.application.command.dto.UpdateProductResult;
import com.b2b.inventory.application.exception.ProductAlreadyExistsException;
import com.b2b.inventory.application.exception.ProductNotFoundException;
import com.b2b.inventory.application.port.out.ProductRepositoryPort;
import com.b2b.inventory.domain.enumtype.ProductStatus;
import com.b2b.inventory.domain.enumtype.ProductUnit;
import com.b2b.inventory.domain.model.Product;
import com.b2b.inventory.domain.valueobject.Sku;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UpdateProductCommandHandlerTest {

    private ProductRepositoryPort productRepositoryPort;
    private UpdateProductCommandHandler handler;

    @BeforeEach
    void setUp() {
        productRepositoryPort = mock(ProductRepositoryPort.class);
        handler = new UpdateProductCommandHandler(productRepositoryPort);
    }

    @Test
    void shouldUpdateProductWhenProductExistsAndSkuIsUnique() {
        UUID productId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        Product existingProduct = activeProduct(productId, companyId);

        UpdateProductCommand command = new UpdateProductCommand(
                productId,
                "laptop-002",
                "Updated Laptop",
                "Updated description",
                ProductUnit.PIECE
        );

        when(productRepositoryPort.findById(productId))
                .thenReturn(Optional.of(existingProduct));

        when(productRepositoryPort.existsByCompanyIdAndSkuAndIdNot(
                eq(companyId),
                any(Sku.class),
                eq(productId)
        )).thenReturn(false);

        when(productRepositoryPort.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UpdateProductResult result = handler.handle(command);

        assertEquals(productId, result.id());
        assertEquals(companyId, result.companyId());
        assertEquals("LAPTOP-002", result.sku());
        assertEquals("Updated Laptop", result.name());
        assertEquals("Updated description", result.description());
        assertEquals(ProductUnit.PIECE, result.unit());
        assertEquals(ProductStatus.ACTIVE, result.status());

        verify(productRepositoryPort).findById(productId);
        verify(productRepositoryPort).existsByCompanyIdAndSkuAndIdNot(
                eq(companyId),
                any(Sku.class),
                eq(productId)
        );
        verify(productRepositoryPort).save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        UUID productId = UUID.randomUUID();

        UpdateProductCommand command = new UpdateProductCommand(
                productId,
                "laptop-002",
                "Updated Laptop",
                "Updated description",
                ProductUnit.PIECE
        );

        when(productRepositoryPort.findById(productId))
                .thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> handler.handle(command)
        );

        assertEquals("Product not found with id: " + productId, exception.getMessage());

        verify(productRepositoryPort).findById(productId);
        verify(productRepositoryPort, never()).save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenAnotherProductHasSameSkuInSameCompany() {
        UUID productId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        Product existingProduct = activeProduct(productId, companyId);

        UpdateProductCommand command = new UpdateProductCommand(
                productId,
                "laptop-002",
                "Updated Laptop",
                "Updated description",
                ProductUnit.PIECE
        );

        when(productRepositoryPort.findById(productId))
                .thenReturn(Optional.of(existingProduct));

        when(productRepositoryPort.existsByCompanyIdAndSkuAndIdNot(
                eq(companyId),
                any(Sku.class),
                eq(productId)
        )).thenReturn(true);

        ProductAlreadyExistsException exception = assertThrows(
                ProductAlreadyExistsException.class,
                () -> handler.handle(command)
        );

        assertEquals("Another product already exists with SKU: LAPTOP-002", exception.getMessage());

        verify(productRepositoryPort).findById(productId);
        verify(productRepositoryPort).existsByCompanyIdAndSkuAndIdNot(
                eq(companyId),
                any(Sku.class),
                eq(productId)
        );
        verify(productRepositoryPort, never()).save(any(Product.class));
    }

    private Product activeProduct(UUID productId, UUID companyId) {
        return Product.restore(
                productId,
                companyId,
                Sku.of("LAPTOP-001"),
                "Gaming Laptop",
                "Old description",
                ProductUnit.PIECE,
                ProductStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );
    }
}