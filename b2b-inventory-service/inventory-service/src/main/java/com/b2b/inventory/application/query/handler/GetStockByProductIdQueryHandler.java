package com.b2b.inventory.application.query.handler;

import com.b2b.inventory.application.exception.StockNotFoundException;
import com.b2b.inventory.application.port.in.GetStockByProductIdUseCase;
import com.b2b.inventory.application.port.out.StockRepositoryPort;
import com.b2b.inventory.application.query.dto.GetStockByProductIdQuery;
import com.b2b.inventory.application.query.dto.GetStockByProductIdResult;
import com.b2b.inventory.domain.model.Stock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetStockByProductIdQueryHandler implements GetStockByProductIdUseCase {

    private final StockRepositoryPort stockRepositoryPort;

    public GetStockByProductIdQueryHandler(StockRepositoryPort stockRepositoryPort) {
        this.stockRepositoryPort = stockRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public GetStockByProductIdResult handle(GetStockByProductIdQuery query) {
        Stock stock = stockRepositoryPort.findByProductId(query.productId())
                .orElseThrow(() -> new StockNotFoundException(
                        "Stock not found for product id: " + query.productId()
                ));

        return new GetStockByProductIdResult(
                stock.getId(),
                stock.getCompanyId(),
                stock.getProductId(),
                stock.getQuantity().getValue(),
                stock.getCreatedAt(),
                stock.getUpdatedAt()
        );
    }
}