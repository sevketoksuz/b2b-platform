package com.b2b.inventory.application.command.handler;

import com.b2b.inventory.application.command.dto.CreateProductCommand;
import com.b2b.inventory.application.command.dto.CreateProductResult;
import com.b2b.inventory.application.exception.ProductAlreadyExistsException;
import com.b2b.inventory.application.port.in.CreateProductUseCase;
import com.b2b.inventory.application.port.out.ProductRepositoryPort;
import com.b2b.inventory.application.port.out.StockRepositoryPort;
import com.b2b.inventory.domain.model.Product;
import com.b2b.inventory.domain.model.Stock;
import com.b2b.inventory.domain.valueobject.Sku;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateProductCommandHandler implements CreateProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final StockRepositoryPort stockRepositoryPort;

    public CreateProductCommandHandler(
            ProductRepositoryPort productRepositoryPort,
            StockRepositoryPort stockRepositoryPort
    ) {
        this.productRepositoryPort = productRepositoryPort;
        this.stockRepositoryPort = stockRepositoryPort;
    }

    @Override
    @Transactional
    public CreateProductResult handle(CreateProductCommand command) {
        Sku sku = Sku.of(command.sku());

        if (productRepositoryPort.existsByCompanyIdAndSku(command.companyId(), sku)) {
            throw new ProductAlreadyExistsException(
                    "Product already exists with SKU: " + sku.getValue()
            );
        }

        Product product = Product.create(
                command.companyId(),
                sku,
                command.name(),
                command.description(),
                command.unit()
        );

        Product savedProduct = productRepositoryPort.save(product);

        Stock stock = Stock.create(
                savedProduct.getCompanyId(),
                savedProduct.getId()
        );

        stockRepositoryPort.save(stock);

        return new CreateProductResult(
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