package com.b2b.inventory.domain.model;

import com.b2b.inventory.domain.enumtype.ProductStatus;
import com.b2b.inventory.domain.enumtype.ProductUnit;
import com.b2b.inventory.domain.exception.ProductDomainException;
import com.b2b.inventory.domain.valueobject.Sku;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Product {

    private final UUID id;
    private final UUID companyId;
    private Sku sku;
    private String name;
    private String description;
    private ProductUnit unit;
    private ProductStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Product(
            UUID id,
            UUID companyId,
            Sku sku,
            String name,
            String description,
            ProductUnit unit,
            ProductStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        validateName(name);
        validateDescription(description);
        validateUnit(unit);
        validateStatus(status);

        this.id = Objects.requireNonNull(id, "Product id cannot be null.");
        this.companyId = Objects.requireNonNull(companyId, "Company id cannot be null.");
        this.sku = Objects.requireNonNull(sku, "SKU cannot be null.");
        this.name = name.trim();
        this.description = normalizeDescription(description);
        this.unit = unit;
        this.status = status;
        this.createdAt = Objects.requireNonNull(createdAt, "Created date cannot be null.");
        this.updatedAt = updatedAt;
    }

    public static Product create(
            UUID companyId,
            Sku sku,
            String name,
            String description,
            ProductUnit unit
    ) {
        LocalDateTime now = LocalDateTime.now();

        return new Product(
                UUID.randomUUID(),
                companyId,
                sku,
                name,
                description,
                unit,
                ProductStatus.ACTIVE,
                now,
                now
        );
    }

    public static Product restore(
            UUID id,
            UUID companyId,
            Sku sku,
            String name,
            String description,
            ProductUnit unit,
            ProductStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Product(
                id,
                companyId,
                sku,
                name,
                description,
                unit,
                status,
                createdAt,
                updatedAt
        );
    }

    public void updateDetails(
            Sku sku,
            String name,
            String description,
            ProductUnit unit
    ) {
        validateName(name);
        validateDescription(description);
        validateUnit(unit);

        this.sku = Objects.requireNonNull(sku, "SKU cannot be null.");
        this.name = name.trim();
        this.description = normalizeDescription(description);
        this.unit = unit;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        if (this.status == ProductStatus.ACTIVE) {
            throw new ProductDomainException("Product is already active.");
        }

        this.status = ProductStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        if (this.status == ProductStatus.INACTIVE) {
            throw new ProductDomainException("Product is already inactive.");
        }

        this.status = ProductStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.status == ProductStatus.ACTIVE;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ProductDomainException("Product name cannot be blank.");
        }

        if (name.trim().length() < 2) {
            throw new ProductDomainException("Product name must contain at least 2 characters.");
        }

        if (name.trim().length() > 150) {
            throw new ProductDomainException("Product name cannot exceed 150 characters.");
        }
    }

    private void validateDescription(String description) {
        if (description != null && description.trim().length() > 500) {
            throw new ProductDomainException("Product description cannot exceed 500 characters.");
        }
    }

    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        return description.trim();
    }

    private void validateUnit(ProductUnit unit) {
        if (unit == null) {
            throw new ProductDomainException("Product unit cannot be null.");
        }
    }

    private void validateStatus(ProductStatus status) {
        if (status == null) {
            throw new ProductDomainException("Product status cannot be null.");
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public Sku getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ProductUnit getUnit() {
        return unit;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}