package com.b2b.inventory.application.port.in;

import com.b2b.inventory.application.command.dto.ChangeProductStatusResult;
import com.b2b.inventory.application.command.dto.DeactivateProductCommand;

public interface DeactivateProductUseCase {

    ChangeProductStatusResult handle(DeactivateProductCommand command);
}