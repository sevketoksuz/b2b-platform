package com.b2b.inventory.application.command.handler;

import com.b2b.inventory.application.command.dto.UpdateProductCommand;
import com.b2b.inventory.application.command.dto.UpdateProductResult;
import com.b2b.inventory.application.exception.ProductAlreadyExistsException;
import com.b2b.inventory.application.exception.ProductNotFoundException;
import com.b2b.inventory.application.port.in.UpdateProductUseCase;
import com.b2b.inventory.application.port.out.ProductRepositoryPort;
import com.b2b.inventory.domain.model.Product;
import com.b2b.inventory.domain.valueobject.Sku;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateProductCommandHandler implements UpdateProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    public UpdateProductCommandHandler(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    @Transactional
    public UpdateProductResult handle(UpdateProductCommand command) {
        Product product = productRepositoryPort.findById(command.id())
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product not found with id: " + command.id()
                ));

        Sku sku = Sku.of(command.sku());

        if (productRepositoryPort.existsByCompanyIdAndSkuAndIdNot(
                product.getCompanyId(),
                sku,
                product.getId()
        )) {
            throw new ProductAlreadyExistsException(
                    "Another product already exists with SKU: " + sku.getValue()
            );
        }

        product.updateDetails(
                sku,
                command.name(),
                command.description(),
                command.unit()
        );

        Product savedProduct = productRepositoryPort.save(product);

        return new UpdateProductResult(
                savedProduct.getId(),
                savedProduct.getCompanyId(),
                savedProduct.getSku().getValue(),
                savedProduct.getName(),
                savedProduct.getDescription(),
                savedProduct.getUnit(),
                savedProduct.getStatus(),
                savedProduct.getCreatedAt()
        );
    }
}