package com.b2b.inventory.application.command.handler;

import com.b2b.inventory.application.command.dto.ChangeProductStatusResult;
import com.b2b.inventory.application.command.dto.DeactivateProductCommand;
import com.b2b.inventory.application.exception.ProductNotFoundException;
import com.b2b.inventory.application.port.in.DeactivateProductUseCase;
import com.b2b.inventory.application.port.out.ProductRepositoryPort;
import com.b2b.inventory.domain.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeactivateProductCommandHandler implements DeactivateProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    public DeactivateProductCommandHandler(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    @Transactional
    public ChangeProductStatusResult handle(DeactivateProductCommand command) {
        Product product = productRepositoryPort.findById(command.id())
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product not found with id: " + command.id()
                ));

        product.deactivate();

        Product savedProduct = productRepositoryPort.save(product);

        return new ChangeProductStatusResult(
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