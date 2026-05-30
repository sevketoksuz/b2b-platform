package com.b2b.inventory.application.port.in;

import com.b2b.inventory.application.command.dto.CreateProductCommand;
import com.b2b.inventory.application.command.dto.CreateProductResult;

public interface CreateProductUseCase {

    CreateProductResult handle(CreateProductCommand command);
}