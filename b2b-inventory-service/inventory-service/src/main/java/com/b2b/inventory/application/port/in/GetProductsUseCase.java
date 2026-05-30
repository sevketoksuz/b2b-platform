package com.b2b.inventory.application.port.in;

import com.b2b.inventory.application.query.dto.GetProductsQuery;
import com.b2b.inventory.application.query.dto.GetProductsResult;

public interface GetProductsUseCase {

    GetProductsResult handle(GetProductsQuery query);
}