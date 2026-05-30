package com.b2b.inventory.application.command.handler;

import com.b2b.inventory.application.command.dto.ChangeProductStatusResult;
import com.b2b.inventory.application.command.dto.DeactivateProductCommand;
import com.b2b.inventory.application.exception.ProductNotFoundException;
import com.b2b.inventory.application.port.out.ProductRepositoryPort;
import com.b2b.inventory.domain.enumtype.ProductStatus;
import com.b2b.inventory.domain.enumtype.ProductUnit;
import com.b2b.inventory.domain.exception.ProductDomainException;
import com.b2b.inventory.domain.model.Product;
import com.b2b.inventory.domain.valueobject.Sku;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeactivateProductCommandHandlerTest {

    private ProductRepositoryPort productRepositoryPort;
    private DeactivateProductCommandHandler handler;

    @BeforeEach
    void setUp() {
        productRepositoryPort = mock(ProductRepositoryPort.class);
        handler = new DeactivateProductCommandHandler(productRepositoryPort);
    }

    @Test
    void shouldDeactivateProductWhenProductExistsAndIsActive() {
        UUID productId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        Product product = product(productId, companyId, ProductStatus.ACTIVE);

        DeactivateProductCommand command = new DeactivateProductCommand(productId);

        when(productRepositoryPort.findById(productId))
                .thenReturn(Optional.of(product));

        when(productRepositoryPort.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ChangeProductStatusResult result = handler.handle(command);

        assertEquals(productId, result.id());
        assertEquals(companyId, result.companyId());
        assertEquals("LAPTOP-001", result.sku());
        assertEquals(ProductStatus.INACTIVE, result.status());

        verify(productRepositoryPort).findById(productId);
        verify(productRepositoryPort).save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        UUID productId = UUID.randomUUID();

        DeactivateProductCommand command = new DeactivateProductCommand(productId);

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
    void shouldThrowExceptionWhenProductAlreadyInactive() {
        UUID productId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        Product product = product(productId, companyId, ProductStatus.INACTIVE);

        DeactivateProductCommand command = new DeactivateProductCommand(productId);

        when(productRepositoryPort.findById(productId))
                .thenReturn(Optional.of(product));

        ProductDomainException exception = assertThrows(
                ProductDomainException.class,
                () -> handler.handle(command)
        );

        assertEquals("Product is already inactive.", exception.getMessage());

        verify(productRepositoryPort).findById(productId);
        verify(productRepositoryPort, never()).save(any(Product.class));
    }

    private Product product(UUID productId, UUID companyId, ProductStatus status) {
        return Product.restore(
                productId,
                companyId,
                Sku.of("LAPTOP-001"),
                "Gaming Laptop",
                "Description",
                ProductUnit.PIECE,
                status,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );
    }
}