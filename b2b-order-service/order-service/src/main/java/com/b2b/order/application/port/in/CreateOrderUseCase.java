package com.b2b.order.application.port.in;

import com.b2b.order.application.command.dto.CreateOrderCommand;
import com.b2b.order.application.command.dto.CreateOrderResult;

public interface CreateOrderUseCase {

    CreateOrderResult handle(CreateOrderCommand command);
}