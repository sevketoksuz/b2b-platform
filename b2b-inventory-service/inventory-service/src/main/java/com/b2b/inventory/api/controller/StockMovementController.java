package com.b2b.inventory.api.controller;

import com.b2b.inventory.api.mapper.StockMovementApiMapper;
import com.b2b.inventory.api.response.PagedResponse;
import com.b2b.inventory.api.response.StockMovementListItemResponse;
import com.b2b.inventory.application.port.in.GetStockMovementsUseCase;
import com.b2b.inventory.application.query.dto.GetStockMovementsQuery;
import com.b2b.inventory.application.query.dto.GetStockMovementsResult;
import com.b2b.inventory.domain.enumtype.StockMovementType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class StockMovementController {

    private final GetStockMovementsUseCase getStockMovementsUseCase;
    private final StockMovementApiMapper stockMovementApiMapper;

    public StockMovementController(
            GetStockMovementsUseCase getStockMovementsUseCase,
            StockMovementApiMapper stockMovementApiMapper
    ) {
        this.getStockMovementsUseCase = getStockMovementsUseCase;
        this.stockMovementApiMapper = stockMovementApiMapper;
    }

    @GetMapping("/stock-movements")
    public ResponseEntity<PagedResponse<StockMovementListItemResponse>> getStockMovements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) UUID productId,
            @RequestParam(required = false) StockMovementType movementType,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime toDate
    ) {
        GetStockMovementsQuery query = new GetStockMovementsQuery(
                page,
                size,
                companyId,
                productId,
                movementType,
                fromDate,
                toDate
        );

        GetStockMovementsResult result = getStockMovementsUseCase.handle(query);

        PagedResponse<StockMovementListItemResponse> response =
                stockMovementApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/products/{productId}/stock-movements")
    public ResponseEntity<PagedResponse<StockMovementListItemResponse>> getProductStockMovements(
            @PathVariable UUID productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) StockMovementType movementType,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime toDate
    ) {
        GetStockMovementsQuery query = new GetStockMovementsQuery(
                page,
                size,
                companyId,
                productId,
                movementType,
                fromDate,
                toDate
        );

        GetStockMovementsResult result = getStockMovementsUseCase.handle(query);

        PagedResponse<StockMovementListItemResponse> response =
                stockMovementApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }
}