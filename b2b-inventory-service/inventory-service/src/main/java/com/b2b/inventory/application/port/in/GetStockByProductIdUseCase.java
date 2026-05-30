package com.b2b.inventory.application.port.in;

import com.b2b.inventory.application.query.dto.GetStockByProductIdQuery;
import com.b2b.inventory.application.query.dto.GetStockByProductIdResult;

public interface GetStockByProductIdUseCase {

    GetStockByProductIdResult handle(GetStockByProductIdQuery query);
}