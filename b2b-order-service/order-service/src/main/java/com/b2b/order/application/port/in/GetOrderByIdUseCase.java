package com.b2b.order.application.port.in;

import com.b2b.order.application.query.dto.GetOrderByIdQuery;
import com.b2b.order.application.query.dto.GetOrderByIdResult;

public interface GetOrderByIdUseCase {

    GetOrderByIdResult handle(GetOrderByIdQuery query);
}