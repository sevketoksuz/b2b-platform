package com.b2b.inventory.application.port.in;

import com.b2b.inventory.application.command.dto.ActivateProductCommand;
import com.b2b.inventory.application.command.dto.ChangeProductStatusResult;

public interface ActivateProductUseCase {

    ChangeProductStatusResult handle(ActivateProductCommand command);
}