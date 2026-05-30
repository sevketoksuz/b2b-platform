package com.b2b.inventory.infrastructure.persistence.mapper;

import com.b2b.inventory.domain.model.StockMovement;
import com.b2b.inventory.domain.valueobject.Quantity;
import com.b2b.inventory.infrastructure.persistence.entity.StockMovementJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class StockMovementPersistenceMapper {

    public StockMovementJpaEntity toJpaEntity(StockMovement stockMovement) {
        return new StockMovementJpaEntity(
                stockMovement.getId(),
                stockMovement.getCompanyId(),
                stockMovement.getProductId(),
                stockMovement.getMovementType(),
                stockMovement.getQuantity().getValue(),
                stockMovement.getPreviousQuantity().getValue(),
                stockMovement.getNewQuantity().getValue(),
                stockMovement.getReason(),
                stockMovement.getCreatedAt()
        );
    }

    public StockMovement toDomainModel(StockMovementJpaEntity entity) {
        return StockMovement.restore(
                entity.getId(),
                entity.getCompanyId(),
                entity.getProductId(),
                entity.getMovementType(),
                Quantity.of(entity.getQuantity()),
                Quantity.of(entity.getPreviousQuantity()),
                Quantity.of(entity.getNewQuantity()),
                entity.getReason(),
                entity.getCreatedAt()
        );
    }
}