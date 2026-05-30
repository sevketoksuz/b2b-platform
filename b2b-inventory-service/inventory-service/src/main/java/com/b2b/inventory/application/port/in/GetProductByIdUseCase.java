package com.b2b.inventory.application.port.in;

import com.b2b.inventory.application.query.dto.GetProductByIdQuery;
import com.b2b.inventory.application.query.dto.GetProductByIdResult;

public interface GetProductByIdUseCase {

    GetProductByIdResult handle(GetProductByIdQuery query);
}