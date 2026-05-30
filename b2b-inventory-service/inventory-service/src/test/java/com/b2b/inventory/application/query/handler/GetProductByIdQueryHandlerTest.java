package com.b2b.inventory.application.query.handler;

import com.b2b.inventory.application.exception.ProductNotFoundException;
import com.b2b.inventory.application.port.out.ProductRepositoryPort;
import com.b2b.inventory.application.query.dto.GetProductByIdQuery;
import com.b2b.inventory.application.query.dto.GetProductByIdResult;
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
import static org.mockito.Mockito.*;

class GetProductByIdQueryHandlerTest {

    private ProductRepositoryPort productRepositoryPort;
    private GetProductByIdQueryHandler handler;

    @BeforeEach
    void setUp() {
        productRepositoryPort = mock(ProductRepositoryPort.class);
        handler = new GetProductByIdQueryHandler(productRepositoryPort);
    }

    @Test
    void shouldReturnProductWhenProductExists() {
        UUID productId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        Product product = product(productId, companyId);

        GetProductByIdQuery query = new GetProductByIdQuery(productId);

        when(productRepositoryPort.findById(productId))
                .thenReturn(Optional.of(product));

        GetProductByIdResult result = handler.handle(query);

        assertEquals(productId, result.id());
        assertEquals(companyId, result.companyId());
        assertEquals("MOUSE-001", result.sku());
        assertEquals("Gaming Mouse", result.name());
        assertEquals("Wireless gaming mouse", result.description());
        assertEquals(ProductUnit.PIECE, result.unit());
        assertEquals(ProductStatus.ACTIVE, result.status());
        assertNotNull(result.createdAt());

        verify(productRepositoryPort).findById(productId);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        UUID productId = UUID.randomUUID();

        GetProductByIdQuery query = new GetProductByIdQuery(productId);

        when(productRepositoryPort.findById(productId))
                .thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> handler.handle(query)
        );

        assertEquals("Product not found with id: " + productId, exception.getMessage());

        verify(productRepositoryPort).findById(productId);
    }

    private Product product(UUID productId, UUID companyId) {
        return Product.restore(
                productId,
                companyId,
                Sku.of("MOUSE-001"),
                "Gaming Mouse",
                "Wireless gaming mouse",
                ProductUnit.PIECE,
                ProductStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );
    }
}