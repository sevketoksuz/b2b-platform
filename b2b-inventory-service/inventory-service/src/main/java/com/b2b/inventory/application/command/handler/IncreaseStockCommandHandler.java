package com.b2b.inventory.application.command.handler;

import com.b2b.inventory.application.command.dto.ChangeStockResult;
import com.b2b.inventory.application.command.dto.IncreaseStockCommand;
import com.b2b.inventory.application.exception.ProductNotActiveException;
import com.b2b.inventory.application.exception.ProductNotFoundException;
import com.b2b.inventory.application.exception.StockNotFoundException;
import com.b2b.inventory.application.port.in.IncreaseStockUseCase;
import com.b2b.inventory.application.port.out.ProductRepositoryPort;
import com.b2b.inventory.application.port.out.StockMovementRepositoryPort;
import com.b2b.inventory.application.port.out.StockRepositoryPort;
import com.b2b.inventory.domain.model.Product;
import com.b2b.inventory.domain.model.Stock;
import com.b2b.inventory.domain.model.StockMovement;
import com.b2b.inventory.domain.valueobject.Quantity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncreaseStockCommandHandler implements IncreaseStockUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final StockRepositoryPort stockRepositoryPort;
    private final StockMovementRepositoryPort stockMovementRepositoryPort;

    public IncreaseStockCommandHandler(
            ProductRepositoryPort productRepositoryPort,
            StockRepositoryPort stockRepositoryPort,
            StockMovementRepositoryPort stockMovementRepositoryPort
    ) {
        this.productRepositoryPort = productRepositoryPort;
        this.stockRepositoryPort = stockRepositoryPort;
        this.stockMovementRepositoryPort = stockMovementRepositoryPort;
    }

    @Override
    @Transactional
    public ChangeStockResult handle(IncreaseStockCommand command) {
        Product product = getActiveProduct(command.companyId(), command.productId());

        Stock stock = stockRepositoryPort.findByCompanyIdAndProductId(
                command.companyId(),
                product.getId()
        ).orElseThrow(() -> new StockNotFoundException(
                "Stock not found for product id: " + product.getId()
        ));

        Quantity previousQuantity = stock.getQuantity();
        Quantity amount = Quantity.of(command.quantity());

        stock.increase(amount);

        Stock savedStock = stockRepositoryPort.save(stock);

        StockMovement movement = StockMovement.createIncrease(
                savedStock.getCompanyId(),
                savedStock.getProductId(),
                amount,
                previousQuantity,
                savedStock.getQuantity(),
                command.reason()
        );

        stockMovementRepositoryPort.save(movement);

        return toResult(savedStock);
    }

    private Product getActiveProduct(java.util.UUID companyId, java.util.UUID productId) {
        Product product = productRepositoryPort.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product not found with id: " + productId
                ));

        if (!product.getCompanyId().equals(companyId)) {
            throw new ProductNotFoundException("Product not found with id: " + productId);
        }

        if (!product.isActive()) {
            throw new ProductNotActiveException(
                    "Cannot change stock for inactive product: " + productId
            );
        }

        return product;
    }

    private ChangeStockResult toResult(Stock stock) {
        return new ChangeStockResult(
                stock.getId(),
                stock.getCompanyId(),
                stock.getProductId(),
                stock.getQuantity().getValue(),
                stock.getCreatedAt(),
                stock.getUpdatedAt()
        );
    }
}