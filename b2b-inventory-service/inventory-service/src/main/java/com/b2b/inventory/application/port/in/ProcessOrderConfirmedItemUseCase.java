package com.b2b.inventory.application.port.in;

import com.b2b.inventory.application.command.dto.ProcessOrderConfirmedItemCommand;

public interface ProcessOrderConfirmedItemUseCase {

    void handle(ProcessOrderConfirmedItemCommand command);

}