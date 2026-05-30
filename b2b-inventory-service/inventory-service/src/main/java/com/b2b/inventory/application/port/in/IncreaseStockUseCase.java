package com.b2b.inventory.application.port.in;

import com.b2b.inventory.application.command.dto.ChangeStockResult;
import com.b2b.inventory.application.command.dto.IncreaseStockCommand;

public interface IncreaseStockUseCase {

    ChangeStockResult handle(IncreaseStockCommand command);
}