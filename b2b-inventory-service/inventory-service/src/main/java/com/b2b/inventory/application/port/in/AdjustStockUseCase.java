package com.b2b.inventory.application.port.in;

import com.b2b.inventory.application.command.dto.AdjustStockCommand;
import com.b2b.inventory.application.command.dto.ChangeStockResult;

public interface AdjustStockUseCase {

    ChangeStockResult handle(AdjustStockCommand command);
}