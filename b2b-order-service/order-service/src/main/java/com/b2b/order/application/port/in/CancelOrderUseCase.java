package com.b2b.order.application.port.in;

import com.b2b.order.application.command.dto.CancelOrderCommand;
import com.b2b.order.application.command.dto.ChangeOrderStatusResult;

public interface CancelOrderUseCase {

    ChangeOrderStatusResult handle(CancelOrderCommand command);
}