package com.b2b.inventory.api.mapper;

import com.b2b.inventory.api.request.CreateProductRequest;
import com.b2b.inventory.api.request.UpdateProductRequest;
import com.b2b.inventory.api.response.PagedResponse;
import com.b2b.inventory.api.response.ProductListItemResponse;
import com.b2b.inventory.api.response.ProductResponse;
import com.b2b.inventory.application.command.dto.ChangeProductStatusResult;
import com.b2b.inventory.application.command.dto.CreateProductCommand;
import com.b2b.inventory.application.command.dto.CreateProductResult;
import com.b2b.inventory.application.command.dto.UpdateProductCommand;
import com.b2b.inventory.application.command.dto.UpdateProductResult;
import com.b2b.inventory.application.query.dto.GetProductByIdResult;
import com.b2b.inventory.application.query.dto.GetProductsResult;
import com.b2b.inventory.application.query.dto.ProductListItemResult;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProductApiMapper {

    public CreateProductCommand toCommand(CreateProductRequest request) {
        return new CreateProductCommand(
                request.companyId(),
                request.sku(),
                request.name(),
                request.description(),
                request.unit()
        );
    }

    public UpdateProductCommand toCommand(UUID id, UpdateProductRequest request) {
        return new UpdateProductCommand(
                id,
                request.sku(),
                request.name(),
                request.description(),
                request.unit()
        );
    }

    public ProductResponse toResponse(CreateProductResult result) {
        return new ProductResponse(
                result.id(),
                result.companyId(),
                result.sku(),
                result.name(),
                result.description(),
                result.unit(),
                result.status(),
                result.createdAt()
        );
    }

    public ProductResponse toResponse(GetProductByIdResult result) {
        return new ProductResponse(
                result.id(),
                result.companyId(),
                result.sku(),
                result.name(),
                result.description(),
                result.unit(),
                result.status(),
                result.createdAt()
        );
    }

    public ProductResponse toResponse(UpdateProductResult result) {
        return new ProductResponse(
                result.id(),
                result.companyId(),
                result.sku(),
                result.name(),
                result.description(),
                result.unit(),
                result.status(),
                result.createdAt()
        );
    }

    public ProductResponse toResponse(ChangeProductStatusResult result) {
        return new ProductResponse(
                result.id(),
                result.companyId(),
                result.sku(),
                result.name(),
                result.description(),
                result.unit(),
                result.status(),
                result.createdAt()
        );
    }

    public PagedResponse<ProductListItemResponse> toResponse(GetProductsResult result) {
        return new PagedResponse<>(
                result.content()
                        .stream()
                        .map(this::toListItemResponse)
                        .toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }

    private ProductListItemResponse toListItemResponse(ProductListItemResult result) {
        return new ProductListItemResponse(
                result.id(),
                result.companyId(),
                result.sku(),
                result.name(),
                result.unit(),
                result.status(),
                result.createdAt()
        );
    }
}