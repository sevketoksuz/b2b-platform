package com.b2b.inventory.application.port.in;

import com.b2b.inventory.application.command.dto.UpdateProductCommand;
import com.b2b.inventory.application.command.dto.UpdateProductResult;

public interface UpdateProductUseCase {

    UpdateProductResult handle(UpdateProductCommand command);
}