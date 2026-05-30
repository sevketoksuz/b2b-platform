package com.b2b.inventory.application.query.handler;

import com.b2b.inventory.application.port.in.GetStockMovementsUseCase;
import com.b2b.inventory.application.port.out.PagedResult;
import com.b2b.inventory.application.port.out.StockMovementRepositoryPort;
import com.b2b.inventory.application.port.out.StockMovementSearchCriteria;
import com.b2b.inventory.application.query.dto.GetStockMovementsQuery;
import com.b2b.inventory.application.query.dto.GetStockMovementsResult;
import com.b2b.inventory.application.query.dto.StockMovementListItemResult;
import com.b2b.inventory.domain.model.StockMovement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetStockMovementsQueryHandler implements GetStockMovementsUseCase {

    private final StockMovementRepositoryPort stockMovementRepositoryPort;

    public GetStockMovementsQueryHandler(
            StockMovementRepositoryPort stockMovementRepositoryPort
    ) {
        this.stockMovementRepositoryPort = stockMovementRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public GetStockMovementsResult handle(GetStockMovementsQuery query) {
        validatePagination(query.page(), query.size());
        validateDateRange(query);

        StockMovementSearchCriteria criteria = new StockMovementSearchCriteria(
                query.page(),
                query.size(),
                query.companyId(),
                query.productId(),
                query.movementType(),
                query.fromDate(),
                query.toDate()
        );

        PagedResult<StockMovement> pagedStockMovements =
                stockMovementRepositoryPort.findStockMovements(criteria);

        List<StockMovementListItemResult> content = pagedStockMovements.content()
                .stream()
                .map(this::toListItemResult)
                .toList();

        return new GetStockMovementsResult(
                content,
                pagedStockMovements.page(),
                pagedStockMovements.size(),
                pagedStockMovements.totalElements(),
                pagedStockMovements.totalPages()
        );
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative.");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Size must be between 1 and 100.");
        }
    }

    private void validateDateRange(GetStockMovementsQuery query) {
        if (
                query.fromDate() != null &&
                        query.toDate() != null &&
                        query.fromDate().isAfter(query.toDate())
        ) {
            throw new IllegalArgumentException("From date cannot be after to date.");
        }
    }

    private StockMovementListItemResult toListItemResult(StockMovement stockMovement) {
        return new StockMovementListItemResult(
                stockMovement.getId(),
                stockMovement.getCompanyId(),
                stockMovement.getProductId(),
                stockMovement.getMovementType(),
                stockMovement.getQuantity().getValue(),
                stockMovement.getPreviousQuantity().getValue(),
                stockMovement.getNewQuantity().getValue(),
                stockMovement.getReason(),
                stockMovement.getCreatedAt()
        );
    }
}