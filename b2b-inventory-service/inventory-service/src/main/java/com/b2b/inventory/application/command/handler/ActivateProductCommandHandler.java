package com.b2b.inventory.application.command.handler;

import com.b2b.inventory.application.command.dto.ActivateProductCommand;
import com.b2b.inventory.application.command.dto.ChangeProductStatusResult;
import com.b2b.inventory.application.exception.ProductNotFoundException;
import com.b2b.inventory.application.port.in.ActivateProductUseCase;
import com.b2b.inventory.application.port.out.ProductRepositoryPort;
import com.b2b.inventory.domain.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivateProductCommandHandler implements ActivateProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    public ActivateProductCommandHandler(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    @Transactional
    public ChangeProductStatusResult handle(ActivateProductCommand command) {
        Product product = productRepositoryPort.findById(command.id())
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product not found with id: " + command.id()
                ));

        product.activate();

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