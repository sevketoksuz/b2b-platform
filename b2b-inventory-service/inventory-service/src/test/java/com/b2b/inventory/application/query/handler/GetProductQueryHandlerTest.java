package com.b2b.inventory.application.query.handler;

import com.b2b.inventory.application.port.out.PagedResult;
import com.b2b.inventory.application.port.out.ProductRepositoryPort;
import com.b2b.inventory.application.port.out.ProductSearchCriteria;
import com.b2b.inventory.application.query.dto.GetProductsQuery;
import com.b2b.inventory.application.query.dto.GetProductsResult;
import com.b2b.inventory.domain.enumtype.ProductStatus;
import com.b2b.inventory.domain.enumtype.ProductUnit;
import com.b2b.inventory.domain.model.Product;
import com.b2b.inventory.domain.valueobject.Sku;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetProductsQueryHandlerTest {

    private ProductRepositoryPort productRepositoryPort;
    private GetProductsQueryHandler handler;

    @BeforeEach
    void setUp() {
        productRepositoryPort = mock(ProductRepositoryPort.class);
        handler = new GetProductsQueryHandler(productRepositoryPort);
    }

    @Test
    void shouldReturnPagedProductsWhenQueryIsValid() {
        UUID companyId = UUID.randomUUID();

        Product firstProduct = product(
                UUID.randomUUID(),
                companyId,
                "MOUSE-001",
                "Gaming Mouse"
        );

        Product secondProduct = product(
                UUID.randomUUID(),
                companyId,
                "KEYBOARD-001",
                "Mechanical Keyboard"
        );

        GetProductsQuery query = new GetProductsQuery(
                0,
                10,
                companyId,
                ProductStatus.ACTIVE,
                ProductUnit.PIECE,
                "gaming"
        );

        when(productRepositoryPort.findProducts(any(ProductSearchCriteria.class)))
                .thenReturn(new PagedResult<>(
                        List.of(firstProduct, secondProduct),
                        0,
                        10,
                        2,
                        1
                ));

        GetProductsResult result = handler.handle(query);

        assertEquals(2, result.content().size());
        assertEquals(0, result.page());
        assertEquals(10, result.size());
        assertEquals(2, result.totalElements());
        assertEquals(1, result.totalPages());

        assertEquals("MOUSE-001", result.content().get(0).sku());
        assertEquals("Gaming Mouse", result.content().get(0).name());

        assertEquals("KEYBOARD-001", result.content().get(1).sku());
        assertEquals("Mechanical Keyboard", result.content().get(1).name());

        ArgumentCaptor<ProductSearchCriteria> criteriaCaptor =
                ArgumentCaptor.forClass(ProductSearchCriteria.class);

        verify(productRepositoryPort).findProducts(criteriaCaptor.capture());

        ProductSearchCriteria criteria = criteriaCaptor.getValue();

        assertEquals(0, criteria.page());
        assertEquals(10, criteria.size());
        assertEquals(companyId, criteria.companyId());
        assertEquals(ProductStatus.ACTIVE, criteria.status());
        assertEquals(ProductUnit.PIECE, criteria.unit());
        assertEquals("gaming", criteria.search());
    }

    @Test
    void shouldThrowExceptionWhenPageIsNegative() {
        GetProductsQuery query = new GetProductsQuery(
                -1,
                10,
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

        verify(productRepositoryPort, never()).findProducts(any(ProductSearchCriteria.class));
    }

    @Test
    void shouldThrowExceptionWhenSizeIsLessThanOne() {
        GetProductsQuery query = new GetProductsQuery(
                0,
                0,
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

        verify(productRepositoryPort, never()).findProducts(any(ProductSearchCriteria.class));
    }

    @Test
    void shouldThrowExceptionWhenSizeIsGreaterThanOneHundred() {
        GetProductsQuery query = new GetProductsQuery(
                0,
                101,
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

        verify(productRepositoryPort, never()).findProducts(any(ProductSearchCriteria.class));
    }

    private Product product(
            UUID productId,
            UUID companyId,
            String sku,
            String name
    ) {
        return Product.restore(
                productId,
                companyId,
                Sku.of(sku),
                name,
                "Description",
                ProductUnit.PIECE,
                ProductStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );
    }
}