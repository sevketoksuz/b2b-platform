package com.b2b.inventory.domain.model;

import com.b2b.inventory.domain.enumtype.ProductStatus;
import com.b2b.inventory.domain.enumtype.ProductUnit;
import com.b2b.inventory.domain.exception.ProductDomainException;
import com.b2b.inventory.domain.valueobject.Sku;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void shouldCreateProductAsActive() {
        UUID companyId = UUID.randomUUID();

        Product product = Product.create(
                companyId,
                Sku.of("LAPTOP-001"),
                "Gaming Laptop",
                "High performance laptop",
                ProductUnit.PIECE
        );

        assertNotNull(product.getId());
        assertEquals(companyId, product.getCompanyId());
        assertEquals("LAPTOP-001", product.getSku().getValue());
        assertEquals("Gaming Laptop", product.getName());
        assertEquals("High performance laptop", product.getDescription());
        assertEquals(ProductUnit.PIECE, product.getUnit());
        assertEquals(ProductStatus.ACTIVE, product.getStatus());
        assertTrue(product.isActive());
        assertNotNull(product.getCreatedAt());
        assertNotNull(product.getUpdatedAt());
    }

    @Test
    void shouldUpdateProductDetails() {
        Product product = Product.create(
                UUID.randomUUID(),
                Sku.of("LAPTOP-001"),
                "Gaming Laptop",
                "Old description",
                ProductUnit.PIECE
        );

        product.updateDetails(
                Sku.of("LAPTOP-002"),
                "Updated Laptop",
                "Updated description",
                ProductUnit.PIECE
        );

        assertEquals("LAPTOP-002", product.getSku().getValue());
        assertEquals("Updated Laptop", product.getName());
        assertEquals("Updated description", product.getDescription());
        assertEquals(ProductUnit.PIECE, product.getUnit());
    }

    @Test
    void shouldDeactivateProduct() {
        Product product = Product.create(
                UUID.randomUUID(),
                Sku.of("LAPTOP-001"),
                "Gaming Laptop",
                null,
                ProductUnit.PIECE
        );

        product.deactivate();

        assertEquals(ProductStatus.INACTIVE, product.getStatus());
        assertFalse(product.isActive());
    }

    @Test
    void shouldActivateInactiveProduct() {
        Product product = Product.create(
                UUID.randomUUID(),
                Sku.of("LAPTOP-001"),
                "Gaming Laptop",
                null,
                ProductUnit.PIECE
        );

        product.deactivate();
        product.activate();

        assertEquals(ProductStatus.ACTIVE, product.getStatus());
        assertTrue(product.isActive());
    }

    @Test
    void shouldThrowExceptionWhenProductAlreadyActive() {
        Product product = Product.create(
                UUID.randomUUID(),
                Sku.of("LAPTOP-001"),
                "Gaming Laptop",
                null,
                ProductUnit.PIECE
        );

        ProductDomainException exception = assertThrows(
                ProductDomainException.class,
                product::activate
        );

        assertEquals("Product is already active.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenProductAlreadyInactive() {
        Product product = Product.create(
                UUID.randomUUID(),
                Sku.of("LAPTOP-001"),
                "Gaming Laptop",
                null,
                ProductUnit.PIECE
        );

        product.deactivate();

        ProductDomainException exception = assertThrows(
                ProductDomainException.class,
                product::deactivate
        );

        assertEquals("Product is already inactive.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenProductNameIsBlank() {
        ProductDomainException exception = assertThrows(
                ProductDomainException.class,
                () -> Product.create(
                        UUID.randomUUID(),
                        Sku.of("LAPTOP-001"),
                        "",
                        null,
                        ProductUnit.PIECE
                )
        );

        assertEquals("Product name cannot be blank.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenProductUnitIsNull() {
        ProductDomainException exception = assertThrows(
                ProductDomainException.class,
                () -> Product.create(
                        UUID.randomUUID(),
                        Sku.of("LAPTOP-001"),
                        "Gaming Laptop",
                        null,
                        null
                )
        );

        assertEquals("Product unit cannot be null.", exception.getMessage());
    }
}