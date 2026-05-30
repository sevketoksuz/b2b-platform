package com.b2b.inventory.application.port.out;

import com.b2b.inventory.domain.model.StockMovement;

public interface StockMovementRepositoryPort {

    StockMovement save(StockMovement stockMovement);

    PagedResult<StockMovement> findStockMovements(StockMovementSearchCriteria criteria);
}