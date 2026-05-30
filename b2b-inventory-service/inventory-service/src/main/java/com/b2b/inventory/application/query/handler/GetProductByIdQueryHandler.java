package com.b2b.inventory.application.query.handler;

import com.b2b.inventory.application.exception.ProductNotFoundException;
import com.b2b.inventory.application.port.in.GetProductByIdUseCase;
import com.b2b.inventory.application.port.out.ProductRepositoryPort;
import com.b2b.inventory.application.query.dto.GetProductByIdQuery;
import com.b2b.inventory.application.query.dto.GetProductByIdResult;
import com.b2b.inventory.domain.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetProductByIdQueryHandler implements GetProductByIdUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    public GetProductByIdQueryHandler(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public GetProductByIdResult handle(GetProductByIdQuery query) {
        Product product = productRepositoryPort.findById(query.id())
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product not found with id: " + query.id()
                ));

        return new GetProductByIdResult(
                product.getId(),
                product.getCompanyId(),
                product.getSku().getValue(),
                product.getName(),
                product.getDescription(),
                product.getUnit(),
                product.getStatus(),
                product.getCreatedAt()
        );
    }
}