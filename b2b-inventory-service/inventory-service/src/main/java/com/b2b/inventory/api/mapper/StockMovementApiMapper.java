package com.b2b.inventory.api.mapper;

import com.b2b.inventory.api.response.PagedResponse;
import com.b2b.inventory.api.response.StockMovementListItemResponse;
import com.b2b.inventory.application.query.dto.GetStockMovementsResult;
import com.b2b.inventory.application.query.dto.StockMovementListItemResult;
import org.springframework.stereotype.Component;

@Component
public class StockMovementApiMapper {

    public PagedResponse<StockMovementListItemResponse> toResponse(
            GetStockMovementsResult result
    ) {
        return new PagedResponse<>(
                result.content()
                        .stream()
                        .map(this::toListItemResponse)
                        .toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }

    private StockMovementListItemResponse toListItemResponse(
            StockMovementListItemResult result
    ) {
        return new StockMovementListItemResponse(
                result.id(),
                result.companyId(),
                result.productId(),
                result.movementType(),
                result.quantity(),
                result.previousQuantity(),
                result.newQuantity(),
                result.reason(),
                result.createdAt()
        );
    }
}