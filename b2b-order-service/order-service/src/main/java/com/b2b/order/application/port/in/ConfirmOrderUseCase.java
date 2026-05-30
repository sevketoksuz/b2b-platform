package com.b2b.order.application.port.in;

import com.b2b.order.application.command.dto.ChangeOrderStatusResult;
import com.b2b.order.application.command.dto.ConfirmOrderCommand;

public interface ConfirmOrderUseCase {

    ChangeOrderStatusResult handle(ConfirmOrderCommand command);
}