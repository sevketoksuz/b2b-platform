package com.b2b.inventory.api.mapper;

import com.b2b.inventory.api.request.AdjustStockRequest;
import com.b2b.inventory.api.request.DecreaseStockRequest;
import com.b2b.inventory.api.request.IncreaseStockRequest;
import com.b2b.inventory.api.response.StockResponse;
import com.b2b.inventory.application.command.dto.AdjustStockCommand;
import com.b2b.inventory.application.command.dto.ChangeStockResult;
import com.b2b.inventory.application.command.dto.DecreaseStockCommand;
import com.b2b.inventory.application.command.dto.IncreaseStockCommand;
import com.b2b.inventory.application.query.dto.GetStockByProductIdResult;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StockApiMapper {

    public IncreaseStockCommand toCommand(UUID productId, IncreaseStockRequest request) {
        return new IncreaseStockCommand(
                request.companyId(),
                productId,
                request.quantity(),
                request.reason()
        );
    }

    public DecreaseStockCommand toCommand(UUID productId, DecreaseStockRequest request) {
        return new DecreaseStockCommand(
                request.companyId(),
                productId,
                request.quantity(),
                request.reason()
        );
    }

    public AdjustStockCommand toCommand(UUID productId, AdjustStockRequest request) {
        return new AdjustStockCommand(
                request.companyId(),
                productId,
                request.newQuantity(),
                request.reason()
        );
    }

    public StockResponse toResponse(GetStockByProductIdResult result) {
        return new StockResponse(
                result.id(),
                result.companyId(),
                result.productId(),
                result.quantity(),
                result.createdAt(),
                result.updatedAt()
        );
    }

    public StockResponse toResponse(ChangeStockResult result) {
        return new StockResponse(
                result.id(),
                result.companyId(),
                result.productId(),
                result.quantity(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}