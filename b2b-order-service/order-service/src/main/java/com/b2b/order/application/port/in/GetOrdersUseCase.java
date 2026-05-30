package com.b2b.order.application.port.in;

import com.b2b.order.application.query.dto.GetOrdersQuery;
import com.b2b.order.application.query.dto.GetOrdersResult;

public interface GetOrdersUseCase {

    GetOrdersResult handle(GetOrdersQuery query);
}