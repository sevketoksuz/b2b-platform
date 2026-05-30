package com.b2b.inventory.application.port.in;

import com.b2b.inventory.application.query.dto.GetStockMovementsQuery;
import com.b2b.inventory.application.query.dto.GetStockMovementsResult;

public interface GetStockMovementsUseCase {

    GetStockMovementsResult handle(GetStockMovementsQuery query);
}