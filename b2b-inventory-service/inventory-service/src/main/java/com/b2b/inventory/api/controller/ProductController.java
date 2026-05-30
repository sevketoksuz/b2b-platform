package com.b2b.inventory.api.controller;

import com.b2b.inventory.api.mapper.ProductApiMapper;
import com.b2b.inventory.api.request.CreateProductRequest;
import com.b2b.inventory.api.request.UpdateProductRequest;
import com.b2b.inventory.api.response.PagedResponse;
import com.b2b.inventory.api.response.ProductListItemResponse;
import com.b2b.inventory.api.response.ProductResponse;
import com.b2b.inventory.application.command.dto.ActivateProductCommand;
import com.b2b.inventory.application.command.dto.ChangeProductStatusResult;
import com.b2b.inventory.application.command.dto.CreateProductCommand;
import com.b2b.inventory.application.command.dto.CreateProductResult;
import com.b2b.inventory.application.command.dto.DeactivateProductCommand;
import com.b2b.inventory.application.command.dto.UpdateProductCommand;
import com.b2b.inventory.application.command.dto.UpdateProductResult;
import com.b2b.inventory.application.port.in.ActivateProductUseCase;
import com.b2b.inventory.application.port.in.CreateProductUseCase;
import com.b2b.inventory.application.port.in.DeactivateProductUseCase;
import com.b2b.inventory.application.port.in.GetProductByIdUseCase;
import com.b2b.inventory.application.port.in.GetProductsUseCase;
import com.b2b.inventory.application.port.in.UpdateProductUseCase;
import com.b2b.inventory.application.query.dto.GetProductByIdQuery;
import com.b2b.inventory.application.query.dto.GetProductByIdResult;
import com.b2b.inventory.application.query.dto.GetProductsQuery;
import com.b2b.inventory.application.query.dto.GetProductsResult;
import com.b2b.inventory.domain.enumtype.ProductStatus;
import com.b2b.inventory.domain.enumtype.ProductUnit;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;
    private final GetProductsUseCase getProductsUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final ActivateProductUseCase activateProductUseCase;
    private final DeactivateProductUseCase deactivateProductUseCase;
    private final ProductApiMapper productApiMapper;

    public ProductController(
            CreateProductUseCase createProductUseCase,
            GetProductByIdUseCase getProductByIdUseCase,
            GetProductsUseCase getProductsUseCase,
            UpdateProductUseCase updateProductUseCase,
            ActivateProductUseCase activateProductUseCase,
            DeactivateProductUseCase deactivateProductUseCase,
            ProductApiMapper productApiMapper
    ) {
        this.createProductUseCase = createProductUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
        this.getProductsUseCase = getProductsUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.activateProductUseCase = activateProductUseCase;
        this.deactivateProductUseCase = deactivateProductUseCase;
        this.productApiMapper = productApiMapper;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request
    ) {
        CreateProductCommand command = productApiMapper.toCommand(request);

        CreateProductResult result = createProductUseCase.handle(command);

        ProductResponse response = productApiMapper.toResponse(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable UUID id
    ) {
        GetProductByIdQuery query = new GetProductByIdQuery(id);

        GetProductByIdResult result = getProductByIdUseCase.handle(query);

        ProductResponse response = productApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ProductListItemResponse>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(required = false) ProductUnit unit,
            @RequestParam(required = false) String search
    ) {
        GetProductsQuery query = new GetProductsQuery(
                page,
                size,
                companyId,
                status,
                unit,
                search
        );

        GetProductsResult result = getProductsUseCase.handle(query);

        PagedResponse<ProductListItemResponse> response = productApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        UpdateProductCommand command = productApiMapper.toCommand(id, request);

        UpdateProductResult result = updateProductUseCase.handle(command);

        ProductResponse response = productApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ProductResponse> activateProduct(
            @PathVariable UUID id
    ) {
        ActivateProductCommand command = new ActivateProductCommand(id);

        ChangeProductStatusResult result = activateProductUseCase.handle(command);

        ProductResponse response = productApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ProductResponse> deactivateProduct(
            @PathVariable UUID id
    ) {
        DeactivateProductCommand command = new DeactivateProductCommand(id);

        ChangeProductStatusResult result = deactivateProductUseCase.handle(command);

        ProductResponse response = productApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }
}