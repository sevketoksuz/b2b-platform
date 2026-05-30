package com.b2b.inventory.infrastructure.persistence.mapper;

import com.b2b.inventory.domain.model.Stock;
import com.b2b.inventory.domain.valueobject.Quantity;
import com.b2b.inventory.infrastructure.persistence.entity.StockJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class StockPersistenceMapper {

    public StockJpaEntity toJpaEntity(Stock stock) {
        return new StockJpaEntity(
                stock.getId(),
                stock.getCompanyId(),
                stock.getProductId(),
                stock.getQuantity().getValue(),
                stock.getCreatedAt(),
                stock.getUpdatedAt()
        );
    }

    public Stock toDomainModel(StockJpaEntity entity) {
        return Stock.restore(
                entity.getId(),
                entity.getCompanyId(),
                entity.getProductId(),
                Quantity.of(entity.getQuantity()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}